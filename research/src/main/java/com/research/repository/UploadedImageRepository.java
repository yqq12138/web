package com.research.repository;

import com.research.model.UploadedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadedImageRepository extends JpaRepository<UploadedImage, Long> {
    // 如果你有其他查询需求，比如按文件名查，也可以加自定义方法
    UploadedImage findByFilename(String filename);
}
