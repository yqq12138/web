package com.research.service;

import com.research.model.UploadedImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    UploadedImage saveUploadedFile(MultipartFile file) throws IOException;
}
