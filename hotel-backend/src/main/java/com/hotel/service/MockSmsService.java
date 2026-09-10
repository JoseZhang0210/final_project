package com.hotel.service;

import org.springframework.stereotype.Service;

@Service
public class MockSmsService implements SmsService {

    @Override
    public String send(String phone, String message) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("請輸入聯絡電話");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("簡訊內容不可空白");
        }

        System.out.println("[測試簡訊] 收件人：" + phone);
        System.out.println("[測試簡訊] 內容：" + message);

        return message;
    }
}
