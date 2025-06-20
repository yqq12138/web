package com.research.controller;

import com.research.model.DetectionRecord;
import com.research.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detect")
public class HistoryController {

    private final HistoryService historyService;

    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    /**
     * 获取所有历史检测记录
     */
    @GetMapping("/history")
    public List<DetectionRecord> getAllHistory() {
        return historyService.getAllRecords();
    }

    @GetMapping("/history/{id}")
    public DetectionRecord getRecordById(@PathVariable Long id) {
        return historyService.getRecordById(id);
    }

}
