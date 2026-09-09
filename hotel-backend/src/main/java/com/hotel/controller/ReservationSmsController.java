package com.hotel.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Reservation;
import com.hotel.service.ReservationService;
import com.hotel.service.SmsService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationSmsController {

    private final ReservationService reservationService;
    private final SmsService smsService;

    public ReservationSmsController(
            ReservationService reservationService,
            SmsService smsService) {

        this.reservationService = reservationService;
        this.smsService = smsService;
    }

    // 測試模式：只在後端 Console 顯示簡訊內容，不會真的寄到手機。
    @PostMapping("/{id}/sms")
    public ResponseEntity<?> sendTestSms(@PathVariable Integer id) {
        Reservation reservation = reservationService.findById(id);

        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String message = "【星澄飯店】測試簡訊：您的訂位已建立，訂位編號 #"
                    + reservation.getReservationId();
            String result = smsService.send(reservation.getContactPhone(), message);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "測試簡訊已發送",
                    "content", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }
}
