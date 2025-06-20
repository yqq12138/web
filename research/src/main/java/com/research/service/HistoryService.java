package com.research.service;

import com.research.model.DetectionRecord;

import java.util.List;

public interface HistoryService {
    List<DetectionRecord> getAllRecords();
    DetectionRecord getRecordById(Long id);

}
