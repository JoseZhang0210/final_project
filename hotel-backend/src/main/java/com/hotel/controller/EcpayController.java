package com.hotel.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.dto.BookingDTO;
import com.hotel.model.dto.BookingPaymentDTO;
import com.hotel.service.BookingPaymentService;
import com.hotel.service.BookingService;
import com.hotel.service.EcpayService;

@RestController
@RequestMapping("/api/payments/ecpay")
public class EcpayController {

    private final EcpayService ecpayService;
    private final BookingService bookingService;
    private final BookingPaymentService bookingPaymentService;

    public EcpayController(EcpayService ecpayService, BookingService bookingService, BookingPaymentService bookingPaymentService) {
        this.ecpayService = ecpayService;
        this.bookingService = bookingService;
        this.bookingPaymentService = bookingPaymentService;
    }

    // 1. 前端結帳時呼叫，取得綠界 HTML 表單
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody Map<String, Integer> request) {
        Integer bookingId = request.get("bookingId");
        if (bookingId == null) {
            return ResponseEntity.badRequest().body("缺少 bookingId");
        }

        BookingDTO booking = bookingService.findById(bookingId).orElseThrow(
            () -> new RuntimeException("找不到訂單")
        );

        // 您可以使用 ngrok 的網址替換 RETURN_URL 以接收回呼
        String RETURN_URL = "https://your-ngrok-url.ngrok-free.app/api/payments/ecpay/callback";
        String CLIENT_BACK_URL = "http://localhost:5173/room-booking-manage";

        String htmlForm = ecpayService.genAioCheckOutHTML(booking, RETURN_URL, CLIENT_BACK_URL);
        return ResponseEntity.ok(htmlForm);
    }

    // 2. 綠界付款成功後的回呼 (Webhook)
    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam Map<String, String> params) {
        System.out.println("收到綠界回呼: " + params);

        // 1. 驗證 CheckMacValue
        if (!ecpayService.verifyCheckMacValue(params)) {
            System.err.println("綠界 CheckMacValue 驗證失敗！");
            return ResponseEntity.badRequest().body("0|CheckMacValue Error");
        }

        // 2. 確認付款結果
        String rtnCode = params.get("RtnCode");
        if ("1".equals(rtnCode)) {
            // 付款成功
            String merchantTradeNo = params.get("MerchantTradeNo"); // ex: HOTEL1T12345
            String tradeNo = params.get("TradeNo"); // 綠界交易序號

            // 解析 Booking ID
            int bookingId = Integer.parseInt(merchantTradeNo.substring(5, merchantTradeNo.indexOf("T")));

            // 尋找對應的 BookingPayment 並更新狀態
            try {
                BookingPaymentDTO payment = bookingPaymentService.findByBookingId(bookingId);
                if (payment != null) {
                    payment.setPaymentStatus("已付款");
                    payment.setTransactionId(tradeNo);
                    payment.setPaidAt(LocalDateTime.now());
                    bookingPaymentService.update(payment.getPaymentId(), payment);
                    System.out.println("訂單 " + bookingId + " 付款狀態已更新為：已付款");
                }
            } catch (Exception e) {
                System.err.println("更新付款狀態失敗: " + e.getMessage());
            }
        }

        // 綠界規定必須回傳 1|OK
        return ResponseEntity.ok("1|OK");
    }
}
