package com.research.controller;

import com.research.model.UploadedImage;
import com.research.service.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/detect")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        System.out.println("===== 后端收到请求 =====");

        System.out.println("收到上传请求，文件名：" + file.getOriginalFilename());
        try {
            UploadedImage savedImage = uploadService.saveUploadedFile(file);
            System.out.println("上传流程结束，返回路径: " + savedImage.getPath());
            return ResponseEntity.ok(Map.of(
                    "message", "上传成功",
                    "path", savedImage.getPath()
            ));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "上传失败：" + e.getMessage()));
        }

    }

}
