package com.research.service;

import com.research.model.DetectionRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface DetectionService {

    /**
     * 根据消息列表进行图文识别，返回AI回复消息列表
     */
    List<Map<String, Object>> detectWithMessages(List<Map<String, Object>> messages) throws Exception;

    /**
     * 根据上传的图片和文本，进行识别分析，返回检测记录
     */
    DetectionRecord analyzeImage(MultipartFile file, String text) throws Exception;
    List<DetectionRecord> analyzeImages(List<MultipartFile> files, String text) throws Exception;

}
