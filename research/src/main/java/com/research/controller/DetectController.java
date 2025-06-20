package com.research.controller;

import com.research.model.DetectionRecord;
import com.research.service.DetectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/detect")
public class DetectController {

    private final DetectService detectService;

    @Autowired
    public DetectController(DetectService detectService) {
        this.detectService = detectService;
    }

    /**
     * 获取最新的检测结果
     */
    @GetMapping("/latest")
    public DetectionRecord getLatestDetect() {
        return detectService.getLatestRecord();
    }

}
