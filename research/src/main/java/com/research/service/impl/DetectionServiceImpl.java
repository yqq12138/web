package com.research.service.impl;

import com.research.model.DetectionRecord;
import com.research.model.UploadedImage;
import com.research.repository.UploadedImageRepository;
import com.research.service.DetectionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.research.repository.DetectionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service

public class DetectionServiceImpl implements DetectionService {

    @Autowired
    private DetectionRecordRepository detectionRecordRepository;
    @Autowired
    private UploadedImageRepository uploadedImageRepository;

    @Value("${qianfan.apiUrl}")
    private String apiUrl;

    @Value("${qianfan.apiKey}")
    private String apiKey;

    @Value("${qianfan.modelName}")
    private String modelName;

    @Value("${upload.dir}")
    private String uploadDir;

    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public List<Map<String, Object>> detectWithMessages(List<Map<String, Object>> messages) throws Exception {
        // 构造请求JSON
        JsonNode rootNode = objectMapper.createObjectNode();
        ((com.fasterxml.jackson.databind.node.ObjectNode) rootNode).put("model", modelName);
        var messagesArray = objectMapper.createArrayNode();
        for (Map<String, Object> msg : messages) {
            var msgNode = objectMapper.createObjectNode();
            msgNode.put("role", msg.getOrDefault("role", "user").toString());
            Object content = msg.get("content");
            if (content instanceof String) {
                msgNode.put("content", (String) content);
            } else {
                // content为复杂结构，直接序列化成JSON
                msgNode.set("content", objectMapper.valueToTree(content));
            }
            messagesArray.add(msgNode);
        }
        ((com.fasterxml.jackson.databind.node.ObjectNode) rootNode).set("messages", messagesArray);
        ((com.fasterxml.jackson.databind.node.ObjectNode) rootNode).put("stream", false);

        String requestJson = objectMapper.writeValueAsString(rootNode);
        String responseJson = postRequest(apiUrl, requestJson, apiKey);

        JsonNode responseNode = objectMapper.readTree(responseJson);
        JsonNode choices = responseNode.path("choices");
        if (!choices.isArray() || choices.size() == 0) {
            throw new RuntimeException("模型返回格式异常");
        }
        JsonNode messageNode = choices.get(0).path("message");
        String content = messageNode.path("content").asText();

        // 解析content，尝试提取analysis文字和坐标信息
        String analysisText = content;
        Map<String, Double> coordinates = null;
        JsonNode contentJson = null;
        try {
            contentJson = objectMapper.readTree(content);
            analysisText = contentJson.path("analysis").asText(analysisText);
            JsonNode coordNode = contentJson.path("coordinates");
            if (coordNode.isObject()) {
                coordinates = objectMapper.convertValue(coordNode, Map.class);
            }
        } catch (Exception e) {
            // 不抛异常，content不是JSON就用纯文本
        }

        // 从用户消息中提取图片Base64
        String base64Image = extractBase64ImageFromMessages(messages);

        // 根据坐标绘制红框图片
        String markedImageBase64 = null;
        if (coordinates != null && base64Image != null) {
            markedImageBase64 = drawRedBoxOnImage(base64Image, coordinates);
        }

        Map<String, Object> aiContent = Map.of(
                "analysis", analysisText,
                "coordinates", coordinates,
                "original_image", base64Image,
                "marked_image", markedImageBase64
        );

        Map<String, Object> assistantMsg = Map.of(
                "role", "assistant",
                "content", aiContent
        );
        if (base64Image != null) {
            DetectionRecord record = new DetectionRecord();
            record.setPartName("对话识别");
            record.setDetectTime(LocalDateTime.now());
            record.setResult(analysisText);
            record.setConfidence(contentJson != null && contentJson.has("confidence") ? contentJson.get("confidence").asDouble() : 0.0);
            record.setImagePath(base64Image);
            record.setMarkedImage(markedImageBase64);
            DetectionRecord saved = detectionRecordRepository.save(record);

            System.out.println("保存成功，记录ID：" + saved.getId());
        }

        return List.of(assistantMsg);

    }

    // 从消息列表中提取第一个Base64格式的图片
    private String extractBase64ImageFromMessages(List<Map<String, Object>> messages) {
        for (Map<String, Object> msg : messages) {
            Object content = msg.get("content");
            if (content instanceof List) {
                for (Object obj : (List<?>) content) {
                    if (obj instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) obj;
                        if ("image_url".equals(map.get("type"))) {
                            Map<?, ?> urlMap = (Map<?, ?>) map.get("image_url");
                            String url = (String) urlMap.get("url");
                            if (url != null && url.startsWith("data:image")) {
                                return url;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    @Transactional
    @Override
    public DetectionRecord analyzeImage(MultipartFile file, String text) throws Exception {
        // 先保存图片，获取UploadedImage，包含Base64和路径
        UploadedImage uploadedImage = saveUploadedImage(file);
        String base64Image = uploadedImage.getPath();

        // 构造请求数据并发送请求，解析返回
        String requestPayload = buildRequestPayload(text == null ? "" : text, base64Image);
        String responseJson = postRequest(apiUrl, requestPayload, apiKey);

        // 解析response
        JsonNode root = objectMapper.readTree(responseJson);
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.size() == 0) {
            throw new RuntimeException("大模型返回数据格式异常");
        }
        JsonNode message = choices.get(0).path("message");
        String content = message.path("content").asText();

        String analysisText = content;
        Map<String, Double> coordinates = null;
        JsonNode contentJson = null;
        try {
            contentJson = objectMapper.readTree(content);
            analysisText = contentJson.path("analysis").asText(analysisText);
            JsonNode coordNode = contentJson.path("coordinates");
            if (coordNode.isObject()) {
                coordinates = objectMapper.convertValue(coordNode, Map.class);
            }
        } catch (Exception e) {
            // 不是JSON格式，内容当纯文本
        }

        String markedImageBase64 = null;
        if (coordinates != null && !coordinates.isEmpty()) {
            markedImageBase64 = drawRedBoxOnImage(base64Image, coordinates);
        }

        double confidence = 0.0;
        if (contentJson != null && contentJson.has("confidence")) {
            confidence = contentJson.get("confidence").asDouble(0.0);
        }

        // 组装检测记录，保存
        DetectionRecord record = new DetectionRecord();
        record.setPartName(text == null || text.isEmpty() ? "未知零件" : text);
        record.setDetectTime(LocalDateTime.now());
        record.setResult(analysisText);
        record.setConfidence(confidence);
        record.setImagePath(base64Image);
        record.setMarkedImage(markedImageBase64);

        DetectionRecord saved = detectionRecordRepository.save(record);
        System.out.println("保存成功，记录ID：" + saved.getId());

        return record;
    }

    @Override
    public List<DetectionRecord> analyzeImages(List<MultipartFile> files, String text) throws Exception {
        List<DetectionRecord> records = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                DetectionRecord record = analyzeImage(file, text);
                records.add(record);
            } catch (Exception e) {
                System.err.println("处理图片失败: " + file.getOriginalFilename() + " - " + e.getMessage());
            }
        }
        return records;
    }


    private String encodeFileToBase64(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            byte[] bytes = is.readAllBytes();
            String ext = getFileExtension(file.getName());
            return "data:image/" + ext + ";base64," + Base64.encodeBase64String(bytes);
        }
    }

    private String getFileExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot > 0 && dot < filename.length() - 1) {
            String ext = filename.substring(dot + 1).toLowerCase();
            if (ext.equals("jpeg")) return "jpeg";
            if (ext.equals("jpg")) return "jpeg";
            return ext;
        }
        return "jpeg";
    }

    private String buildRequestPayload(String text, String imageDataUrl) {
        String safeText = text.replace("\"", "\\\"");
        return String.format("{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"},{\"role\":\"user\",\"content\":[{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\"}}]}],\"stream\":false}",
                modelName, safeText, imageDataUrl);
    }

    private String postRequest(String urlString, String payload, String apiKey) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(15000);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes("UTF-8"));
            os.flush();
        }

        int code = conn.getResponseCode();
        InputStream is = (code == 200) ? conn.getInputStream() : conn.getErrorStream();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            if (code != 200) {
                throw new IOException("HTTP " + code + ": " + response);
            }
            return response.toString();
        }
    }

    private double getDoubleFromMap(Map<String, ?> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return 0.0;
    }



    private String drawRedBoxOnImage(String base64Image, Map<String, ?> coordinates) throws IOException {
        System.out.println("开始绘制红框，坐标：" + coordinates);
        if (base64Image == null || !base64Image.startsWith("data:image")) {
            throw new IllegalArgumentException("无效的base64图片");
        }
        int commaIndex = base64Image.indexOf(",");
        String base64Data = base64Image.substring(commaIndex + 1);
        byte[] imageBytes = Base64.decodeBase64(base64Data);

        InputStream in = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(in);
        if (image == null) {
            throw new IOException("无法读取图片");
        }

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        double left, top, width, height;
        if (coordinates.containsKey("x1") && coordinates.containsKey("y1")
                && coordinates.containsKey("x2") && coordinates.containsKey("y2")) {
            left = getDoubleFromMap(coordinates, "x1");
            top = getDoubleFromMap(coordinates, "y1");
            double x2 = getDoubleFromMap(coordinates, "x2");
            double y2 = getDoubleFromMap(coordinates, "y2");
            width = x2 - left;
            height = y2 - top;
        } else {
            left = getDoubleFromMap(coordinates, "left");
            top = getDoubleFromMap(coordinates, "top");
            width = getDoubleFromMap(coordinates, "width");
            height = getDoubleFromMap(coordinates, "height");

            if (left <= 1 && top <= 1 && width <= 1 && height <= 1) {
                left = left * imgWidth;
                top = top * imgHeight;
                width = width * imgWidth;
                height = height * imgHeight;
            }
        }

        System.out.printf("绘制框坐标（像素）: left=%.2f, top=%.2f, width=%.2f, height=%.2f%n", left, top, width, height);

        if (width <= 0 || height <= 0) {
            System.out.println("警告：宽高小于等于0，跳过绘制");
            return base64Image;  // 不绘制
        }

        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(4));
        g.drawRect((int) left, (int) top, (int) width, (int) height);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        String base64Out = Base64.encodeBase64String(baos.toByteArray());
        System.out.println("绘制完成，返回base64长度：" + base64Out.length());

        return "data:image/png;base64," + base64Out;
    }
    @Transactional
    public UploadedImage saveUploadedImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String ext = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        String newFilename = uuid + "." + ext;

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File destFile = new File(dir, newFilename);
        file.transferTo(destFile);

        // 编码为 Base64
        String base64 = encodeFileToBase64(destFile);

        UploadedImage uploadedImage = new UploadedImage();
        uploadedImage.setFilename(newFilename);
        uploadedImage.setPath(base64);

        // 存库（如需）
        uploadedImageRepository.save(uploadedImage);

        return uploadedImage;
    }



}
