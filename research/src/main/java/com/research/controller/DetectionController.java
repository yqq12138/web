package com.research.controller;

import com.research.model.DetectionRecord;
import com.research.service.DetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/detection")
public class DetectionController {

    private final DetectionService detectionService;

    public DetectionController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> analyzeJsonInput(@RequestBody Map<String, Object> payload) {
        if (payload == null || !payload.containsKey("messages")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "'messages' field is required"
            ));
        }

        try {
            Object messagesObj = payload.get("messages");
            if (!(messagesObj instanceof List)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "'messages' must be a list"
                ));
            }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> messages = (List<Map<String, Object>>) messagesObj;

            List<Map<String, Object>> aiReply = detectionService.detectWithMessages(messages);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "messages", aiReply
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}
