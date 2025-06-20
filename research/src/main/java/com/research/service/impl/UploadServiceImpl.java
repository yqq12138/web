package com.research.service.impl;

import com.research.model.UploadedImage;
import com.research.repository.UploadedImageRepository;
import com.research.service.UploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class UploadServiceImpl implements UploadService {

    private final UploadedImageRepository imageRepository;

    @Value("${upload.folder.path}")
    private String uploadFolderPath;

    public UploadServiceImpl(UploadedImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public UploadedImage saveUploadedFile(MultipartFile file) throws IOException {
        System.out.println("【开始上传】");

        if (file == null) {
            throw new IOException("文件对象为 null！");
        }

        if (file.isEmpty()) {
            throw new IOException("文件为空！");
        }

        System.out.println("原始文件名: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize() + " 字节");

        String projectPath = System.getProperty("user.dir");
        System.out.println("项目根路径: " + projectPath);
        String saveDirPath = projectPath + File.separator + uploadFolderPath;
        System.out.println("保存目录路径: " + saveDirPath);

        // 创建目录
        File uploadDir = new File(saveDirPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            System.out.println("上传目录不存在，已尝试创建，成功？" + created);
        } else {
            System.out.println("上传目录已存在");
        }

        // 生成文件名
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir, filename);
        System.out.println("目标文件完整路径: " + dest.getAbsolutePath());

        try {
            file.transferTo(dest);
            System.out.println("✅ 文件保存成功");
        } catch (IOException e) {
            System.out.println("❌ 文件保存失败：" + e.getMessage());
            throw e;
        }

        // 数据库存储路径
        String dbPath = "/" + uploadFolderPath + "/" + filename;
        System.out.println("数据库中存储的相对路径: " + dbPath);

        UploadedImage image = new UploadedImage();
        image.setFilename(file.getOriginalFilename());
        image.setPath(dbPath);
        image.setUploadTime(LocalDateTime.now());

        // 存数据库
        UploadedImage savedImage = imageRepository.save(image);
        System.out.println("✅ 数据库保存成功，ID: " + savedImage.getId());

        return savedImage;
    }
}
