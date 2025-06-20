package com.research.service.impl;

import com.research.model.DetectionRecord;
import com.research.repository.DetectionRecordRepository;
import com.research.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryServiceImpl implements HistoryService {

    private final DetectionRecordRepository detectionRecordRepository;

    @Autowired
    public HistoryServiceImpl(DetectionRecordRepository detectionRecordRepository) {
        this.detectionRecordRepository = detectionRecordRepository;
    }

    @Override
    public List<DetectionRecord> getAllRecords() {
        return detectionRecordRepository.findAllByOrderByDetectTimeDesc();
    }
    @Override
    public DetectionRecord getRecordById(Long id) {
        return detectionRecordRepository.findById(id).orElse(null);
    }

}
