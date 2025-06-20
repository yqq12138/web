package com.research.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "detection_record")
public class DetectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "part_name", nullable = false, length = 100)
    private String partName;

    @Column(name = "detect_time", nullable = false)
    private LocalDateTime detectTime;

    @Column(name = "result", nullable = false, columnDefinition = "LONGTEXT")
    private String result;

    @Column(name = "confidence", nullable = false)
    private Double confidence;

    @Column(name = "image_path", columnDefinition = "LONGTEXT")
    private String imagePath;

    @Column(columnDefinition = "TEXT")
    private String markedImage;  // 对比图（带缺陷标注）


    public DetectionRecord() {}

    public DetectionRecord(Long id, String partName, LocalDateTime detectTime,
                           String result, Double confidence, String imagePath, String markedImage) {
        this.id = id;
        this.partName = partName;
        this.detectTime = detectTime;
        this.result = result;
        this.confidence = confidence;
        this.imagePath = imagePath;
        this.markedImage = markedImage;
    }


    // getter 和 setter
    public String getMarkedImage() {
        return markedImage;
    }

    public void setMarkedImage(String markedImage) {
        this.markedImage = markedImage;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public LocalDateTime getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(LocalDateTime detectTime) {
        this.detectTime = detectTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
