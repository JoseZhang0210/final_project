package com.hotel.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.dto.BookingDTO;
import com.hotel.model.dto.BookingPaymentDTO;
import com.hotel.service.BookingPaymentService;
import com.hotel.service.BookingService;
import com.hotel.service.RoomBookingEcpayService;

import lombok.extern.slf4j.Slf4j;

/**
 * 綠界 ECPay 訂房金流整合控制器
 * 處理結帳表單產生、金流 Webhook 回呼與瀏覽器跳轉
 */
@Slf4j
@RestController
@RequestMapping("/api/payments/ecpay")
public class RoomBookingEcpayController {

    private final RoomBookingEcpayService ecpayService;
    private final BookingService bookingService;
    private final BookingPaymentService bookingPaymentService;

    @Value("${ecpay.payment.return-url:https://httpbin.org/post}")
    private String ecpayReturnUrl;

    @Value("${ecpay.payment.result-url:http://localhost:8081/api/payments/ecpay/client-return/}")
    private String ecpayResultUrlPrefix;

    @Value("${app.frontend.checkout-url:http://localhost:5173/room-checkout}")
    private String frontendCheckoutUrl;

    public RoomBookingEcpayController(
            RoomBookingEcpayService ecpayService,
            BookingService bookingService,
            BookingPaymentService bookingPaymentService) {
        this.ecpayService = ecpayService;
        this.bookingService = bookingService;
        this.bookingPaymentService = bookingPaymentService;
    }

    /**
     * 前端結帳時呼叫：取得綠界 HTML 表單 (已整合待付款紀錄建立)
     */
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody Map<String, Object> request) {
        Integer bookingId = request.get("bookingId") != null
                ? Integer.valueOf(request.get("bookingId").toString())
                : null;
        String frontendUrl = (String) request.get("frontendUrl");

        if (bookingId == null) {
            return ResponseEntity.badRequest().body("缺少 bookingId");
        }

        BookingDTO booking = bookingService.findById(bookingId).orElseThrow(
                () -> new IllegalArgumentException("找不到訂單 ID: " + bookingId));

        // 階段一：建立/確保「待付款」紀錄 (委派至 Service 封裝，DRY 消除樣板代碼)
        try {
            bookingPaymentService.ensurePendingPayment(bookingId, booking.getBookingPrice(), "信用卡");
        } catch (Exception e) {
            log.error("建立待付款紀錄失敗 (Booking ID: {}): {}", bookingId, e.getMessage());
        }

        String returnUrl;
        String clientBackUrl;

        if (frontendUrl != null && frontendUrl.contains("localhost")) {
            // 本地開發環境：由後端處理再跳轉
            returnUrl = "https://httpbin.org/post";
            clientBackUrl = "http://localhost:8081/api/payments/ecpay/client-return/" + booking.getBookingId()
                    + "?local=true";
        } else {
            // 線上環境：使用環境變數設定的網址
            returnUrl = ecpayReturnUrl;
            if (ecpayResultUrlPrefix.contains("client-return")) {
                clientBackUrl = ecpayResultUrlPrefix + booking.getBookingId();
            } else {
                clientBackUrl = ecpayResultUrlPrefix + "?paymentSuccess=true";
            }
        }

        String htmlForm = ecpayService.genAioCheckOutHTML(booking, returnUrl, clientBackUrl);
        return ResponseEntity.ok(htmlForm);
    }

    /**
     * 綠界付款成功後的回呼 (Webhook)
     */
    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam Map<String, String> params) {
        log.info("收到綠界付款回呼參數: {}", params);

        // 1. 驗證 CheckMacValue 簽名
        if (!ecpayService.verifyCheckMacValue(params)) {
            log.error("綠界 CheckMacValue 驗證簽名失敗！");
            return ResponseEntity.badRequest().body("0|CheckMacValue Error");
        }

        // 2. 確認付款結果並以冪等方式更新紀錄
        processPaymentSuccess(params);

        // 綠界規定必須回傳 1|OK
        return ResponseEntity.ok("1|OK");
    }

    /**
     * 綠界免 Ngrok 零配置：藍色按鈕跳轉 (Client-Return)
     */
    @GetMapping("/client-return/{bookingId}")
    public ResponseEntity<Void> handleClientReturn(
            @PathVariable("bookingId") Integer bookingId,
            @RequestParam(value = "local", required = false) String local) {
        log.info("收到綠界返回按鈕跳轉，訂單編號: {}", bookingId);

        try {
            BookingDTO booking = bookingService.findById(bookingId).orElse(null);
            Integer amount = booking != null ? booking.getBookingPrice() : 0;
            // 透過統一的 Service 處理付款成功 (具備冪等性防護)
            bookingPaymentService.processSuccessfulPayment(bookingId, amount, "信用卡", null);
        } catch (Exception e) {
            log.error("更新付款狀態失敗 (Booking ID: {}): {}", bookingId, e.getMessage());
        }

        // 跳轉回前端結帳完成頁
        String targetUrl;
        if ("true".equals(local)) {
            targetUrl = "http://localhost:5173/room-checkout?paymentSuccess=true";
        } else {
            targetUrl = frontendCheckoutUrl != null ? frontendCheckoutUrl : "http://localhost:5173/room-checkout";
            targetUrl += targetUrl.contains("?") ? "&paymentSuccess=true" : "?paymentSuccess=true";
        }

        targetUrl = targetUrl.trim().replace(" ", "%20");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(targetUrl))
                .build();
    }

    /**
     * 前端輪詢付款狀態 API
     */
    @GetMapping("/status/{bookingId}")
    public ResponseEntity<Map<String, String>> getPaymentStatus(@PathVariable("bookingId") Integer bookingId) {
        BookingPaymentDTO payment = bookingPaymentService.findByBookingId(bookingId);
        Map<String, String> response = new HashMap<>();
        if (payment != null) {
            response.put("status", payment.getPaymentStatus());
        } else {
            response.put("status", "找不到訂單");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 開發測試用：一鍵強制模擬付款成功 (呼叫統一 Service，消除重複代碼)
     */
    @PostMapping("/mock-pay/{bookingId}")
    public ResponseEntity<Map<String, String>> mockPaymentSuccess(@PathVariable("bookingId") Integer bookingId) {
        try {
            BookingDTO booking = bookingService.findById(bookingId).orElse(null);
            Integer amount = booking != null ? booking.getBookingPrice() : 0;
            bookingPaymentService.processSuccessfulPayment(bookingId, amount, "信用卡", null);

            Map<String, String> response = new HashMap<>();
            response.put("message", "模擬付款成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("模擬付款成功發生錯誤 (Booking ID: {}): {}", bookingId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 開發測試用：一鍵強制模擬付款失敗
     */
    @PostMapping("/mock-fail/{bookingId}")
    public ResponseEntity<Map<String, String>> mockPaymentFail(@PathVariable("bookingId") Integer bookingId) {
        try {
            // 1. 將訂單改為已取消
            BookingDTO booking = bookingService.findById(bookingId).orElse(null);
            if (booking != null) {
                booking.setBookingStatus("已取消");
                bookingService.updateBooking(bookingId, booking);
            }

            // 2. 將付款狀態改為付款失敗
            BookingPaymentDTO payment = bookingPaymentService.findByBookingId(bookingId);
            if (payment != null) {
                payment.setPaymentStatus("付款失敗");
                bookingPaymentService.update(payment.getPaymentId(), payment);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "模擬付款失敗");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("模擬付款失敗發生錯誤 (Booking ID: {}): {}", bookingId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 處理綠界 Webhook 回傳成功資訊 (DRY 重構)
     */
    private void processPaymentSuccess(Map<String, String> params) {
        String rtnCode = params.get("RtnCode");
        if ("1".equals(rtnCode)) {
            String merchantTradeNo = params.get("MerchantTradeNo"); // ex: HOTEL1T12345
            String tradeNo = params.get("TradeNo"); // 綠界交易序號
            String tradeAmtStr = params.get("TradeAmt");
            int amount = tradeAmtStr != null ? Integer.parseInt(tradeAmtStr) : 0;

            try {
                int bookingId = Integer.parseInt(merchantTradeNo.substring(5, merchantTradeNo.indexOf("T")));
                bookingPaymentService.processSuccessfulPayment(bookingId, amount, "信用卡", tradeNo);
            } catch (Exception e) {
                log.error("解析或更新綠界付款回呼失敗: {}", e.getMessage(), e);
            }
        }
    }
}
