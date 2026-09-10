package com.hotel.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.dto.SmsRequest;
import com.hotel.service.SmsService;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    // 共用測試簡訊 API，其他模組可傳電話與訊息直接使用。
    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody SmsRequest request) {
        try {
            String message = smsService.send(request.getPhone(), request.getMessage());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "測試簡訊已發送",
                    "content", message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }
}
