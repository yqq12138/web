package com.research.service.impl;

import com.research.model.DetectionRecord;
import com.research.repository.DetectionRecordRepository;
import com.research.service.DetectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetectServiceImpl implements DetectService {

    private final DetectionRecordRepository detectionRecordRepository;

    @Autowired
    public DetectServiceImpl(DetectionRecordRepository detectionRecordRepository) {
        this.detectionRecordRepository = detectionRecordRepository;
    }

    @Override
    public DetectionRecord getLatestRecord() {
        // 按时间倒序取最新一条检测记录
        return detectionRecordRepository.findTopByOrderByDetectTimeDesc();
    }

    @Override
    public List<DetectionRecord> getAllRecords() {
        return detectionRecordRepository.findAll();
    }

    @Override
    public DetectionRecord getRecordById(Long id) {
        return detectionRecordRepository.findById(id).orElse(null);
    }
}
