package com.research.service;

import com.research.model.DetectionRecord;
import java.util.List;

public interface DetectService {
    // 获取最新检测记录
    DetectionRecord getLatestRecord();

    // 获取所有检测记录（以后用）
    List<DetectionRecord> getAllRecords();

    // 根据ID获取检测记录详情
    DetectionRecord getRecordById(Long id);

}
