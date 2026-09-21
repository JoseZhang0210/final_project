package com.hotel.controller;

import java.net.URI;
import java.time.LocalDateTime;
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
import com.hotel.util.MailUtil;

@RestController
@RequestMapping("/api/payments/ecpay")
public class RoomBookingEcpayController {

    private final RoomBookingEcpayService ecpayService;
    private final BookingService bookingService;
    private final BookingPaymentService bookingPaymentService;
    private final MailUtil mailUtil;

    @Value("${ecpay.payment.return-url:https://httpbin.org/post}")
    private String ecpayReturnUrl;

    @Value("${ecpay.payment.result-url:http://localhost:8081/api/payments/ecpay/client-return/}")
    private String ecpayResultUrlPrefix;

    @Value("${app.frontend.checkout-url:http://localhost:5173/room-checkout}")
    private String frontendCheckoutUrl;

    public RoomBookingEcpayController(RoomBookingEcpayService ecpayService, BookingService bookingService,
            BookingPaymentService bookingPaymentService, MailUtil mailUtil) {
        this.ecpayService = ecpayService;
        this.bookingService = bookingService;
        this.bookingPaymentService = bookingPaymentService;
        this.mailUtil = mailUtil;
    }

    // 1. 前端結帳時呼叫，取得綠界 HTML 表單
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody Map<String, Object> request) {
        Integer bookingId = request.get("bookingId") != null ? Integer.valueOf(request.get("bookingId").toString())
                : null;
        String frontendUrl = (String) request.get("frontendUrl");

        if (bookingId == null) {
            return ResponseEntity.badRequest().body("缺少 bookingId");
        }

        BookingDTO booking = bookingService.findById(bookingId).orElseThrow(
                () -> new RuntimeException("找不到訂單"));

        // 階段一：建立「待付款」紀錄
        try {
            BookingPaymentDTO existingPayment = bookingPaymentService.findByBookingId(bookingId);
            if (existingPayment == null) {
                BookingPaymentDTO payment = new BookingPaymentDTO();
                payment.setBookingId(bookingId);
                payment.setAmount(booking.getBookingPrice());
                payment.setPaymentMethod("信用卡");
                payment.setPaymentStatus("待付款");
                payment.setPaidAt(null); // 尚未付款
                bookingPaymentService.createPayment(payment);
            } else {
                existingPayment.setPaymentStatus("待付款");
                existingPayment.setPaidAt(null); // 確保清空付款時間
                bookingPaymentService.update(existingPayment.getPaymentId(), existingPayment);
            }
        } catch (Exception e) {
            System.err.println("建立待付款紀錄失敗: " + e.getMessage());
        }

        String RETURN_URL;
        String CLIENT_BACK_URL;

        if (frontendUrl != null && frontendUrl.contains("localhost")) {
            // 本地開發環境：由後端處理再跳轉
            RETURN_URL = "https://httpbin.org/post";
            CLIENT_BACK_URL = "http://localhost:8081/api/payments/ecpay/client-return/" + booking.getBookingId()
                    + "?local=true";
        } else {
            // 上機環境：使用環境變數設定的網址
            RETURN_URL = ecpayReturnUrl;

            // 如果 teammate 設的是後端網址，補上 bookingId；如果是前端網址，補上成功標記
            if (ecpayResultUrlPrefix.contains("client-return")) {
                CLIENT_BACK_URL = ecpayResultUrlPrefix + booking.getBookingId();
            } else {
                CLIENT_BACK_URL = ecpayResultUrlPrefix + "?paymentSuccess=true";
            }
        }

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
        processPaymentSuccess(params);

        // 綠界規定必須回傳 1|OK
        return ResponseEntity.ok("1|OK");
    }

    // 3. 綠界免 Ngrok 零配置：藍色按鈕跳轉 (Client-Return)
    @GetMapping("/client-return/{bookingId}")
    public ResponseEntity<Void> handleClientReturn(@PathVariable("bookingId") Integer bookingId,
            @RequestParam(value = "local", required = false) String local) {
        System.out.println("收到綠界藍色按鈕跳轉，訂單編號: " + bookingId);

        try {
            // 尋找或建立對應的 BookingPayment 並更新狀態
            BookingPaymentDTO payment = bookingPaymentService.findByBookingId(bookingId);
            // 查出訂單金額
            BookingDTO booking = bookingService.findById(bookingId).orElseThrow(() -> new RuntimeException("訂單不存在"));

            boolean shouldSendMail = false;

            // 產生一組假的交易序號，符合您的圖表 TXN...
            String tradeNo = "TXN"
                    + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + (System.currentTimeMillis() % 10000);

            if (payment == null) {
                payment = new BookingPaymentDTO();
                payment.setBookingId(bookingId);
                payment.setAmount(booking.getBookingPrice());
                payment.setPaymentMethod("信用卡");
                payment.setPaymentStatus("已付款");
                payment.setTransactionId(tradeNo);
                payment.setPaidAt(LocalDateTime.now());
                bookingPaymentService.createPayment(payment);
                shouldSendMail = true;
            } else {
                if (!"已付款".equals(payment.getPaymentStatus())) {
                    shouldSendMail = true;
                }
                payment.setPaymentStatus("已付款");
                if (payment.getTransactionId() == null || payment.getTransactionId().isEmpty()) {
                    payment.setTransactionId(tradeNo);
                }
                payment.setPaidAt(LocalDateTime.now());
                bookingPaymentService.update(payment.getPaymentId(), payment);
            }
            System.out.println("訂單 " + bookingId + " 付款狀態已透過藍色按鈕更新為：已付款");

            if (shouldSendMail) {
                try {
                    mailUtil.sendBookingConfirmation(bookingId);
                } catch (Exception e) {
                    System.err.println("發送訂房確認信失敗: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("更新付款狀態失敗: " + e.getMessage());
        }

        // 統一跳轉到環境變數指定的前端結帳頁面，並帶上成功標記
        // 因為前端已經實作了 localStorage，所以不需要帶一長串參數
        String targetUrl;
        if ("true".equals(local)) {
            targetUrl = "http://localhost:5173/room-checkout?paymentSuccess=true";
        } else {
            targetUrl = frontendCheckoutUrl != null ? frontendCheckoutUrl : "http://localhost:5173/room-checkout";
            if (targetUrl.contains("?")) {
                targetUrl += "&paymentSuccess=true";
            } else {
                targetUrl += "?paymentSuccess=true";
            }
        }

        targetUrl = targetUrl.trim().replace(" ", "%20");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(targetUrl))
                .build();
    }

    // 4. 前端輪詢付款狀態 API
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

    // 5. 開發測試用：一鍵強制模擬付款成功
    @PostMapping("/mock-pay/{bookingId}")
    public ResponseEntity<Map<String, String>> mockPaymentSuccess(@PathVariable("bookingId") Integer bookingId) {
        try {
            BookingPaymentDTO payment = bookingPaymentService.findByBookingId(bookingId);
            if (payment != null && "待付款".equals(payment.getPaymentStatus())) {
                String tradeNo = "TXN"
                        + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                        + (System.currentTimeMillis() % 10000);
                payment.setPaymentStatus("已付款");
                payment.setTransactionId(tradeNo);
                payment.setPaidAt(LocalDateTime.now());
                bookingPaymentService.update(payment.getPaymentId(), payment);

                // 發送訂房確認信
                try {
                    mailUtil.sendBookingConfirmation(bookingId);
                } catch (Exception e) {
                    System.err.println("寄信失敗：" + e.getMessage());
                }
            }
            Map<String, String> response = new HashMap<>();
            response.put("message", "模擬付款成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 6. 開發測試用：一鍵強制模擬付款失敗
    @PostMapping("/mock-fail/{bookingId}")
    public ResponseEntity<Map<String, String>> mockPaymentFail(@PathVariable("bookingId") Integer bookingId) {
        try {
            // 1. 尋找訂單並將狀態改為「已取消」
            BookingDTO booking = bookingService.findById(bookingId).orElse(null);
            if (booking != null) {
                booking.setBookingStatus("已取消");
                bookingService.updateBooking(bookingId, booking);
            }

            // 2. 尋找付款紀錄並將狀態改為「付款失敗」
            BookingPaymentDTO payment = bookingPaymentService.findByBookingId(bookingId);
            if (payment != null) {
                payment.setPaymentStatus("付款失敗");
                // 失敗的付款不設定付款時間與交易序號，保持原始狀態
                bookingPaymentService.update(payment.getPaymentId(), payment);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "模擬付款失敗");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("模擬付款失敗處理錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void processPaymentSuccess(Map<String, String> params) {
        String rtnCode = params.get("RtnCode");
        if ("1".equals(rtnCode)) {
            // 付款成功
            String merchantTradeNo = params.get("MerchantTradeNo"); // ex: HOTEL1T12345
            String tradeNo = params.get("TradeNo"); // 綠界交易序號 (真實的 20 碼)
            String tradeAmtStr = params.get("TradeAmt");
            int amount = tradeAmtStr != null ? Integer.parseInt(tradeAmtStr) : 0;

            // 解析 Booking ID
            int bookingId = Integer.parseInt(merchantTradeNo.substring(5, merchantTradeNo.indexOf("T")));

            try {
                boolean shouldSendMail = false;
                // 尋找或建立對應的 BookingPayment 並更新狀態
                BookingPaymentDTO payment = bookingPaymentService.findByBookingId(bookingId);
                if (payment == null) {
                    payment = new BookingPaymentDTO();
                    payment.setBookingId(bookingId);
                    payment.setAmount(amount);
                    payment.setPaymentMethod("信用卡"); // 依照計畫，拿掉 (ECPay)
                    payment.setPaymentStatus("已付款");
                    payment.setTransactionId(tradeNo);
                    payment.setPaidAt(LocalDateTime.now());
                    bookingPaymentService.createPayment(payment);
                    shouldSendMail = true;
                } else {
                    if (!"已付款".equals(payment.getPaymentStatus())) {
                        shouldSendMail = true;
                    }
                    payment.setPaymentMethod("信用卡");
                    payment.setPaymentStatus("已付款");
                    payment.setTransactionId(tradeNo);
                    payment.setPaidAt(LocalDateTime.now());
                    bookingPaymentService.update(payment.getPaymentId(), payment);
                }
                System.out.println("訂單 " + bookingId + " 付款狀態已更新為：已付款");

                if (shouldSendMail) {
                    try {
                        mailUtil.sendBookingConfirmation(bookingId);
                    } catch (Exception e) {
                        System.err.println("發送訂房確認信失敗: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("更新付款狀態失敗: " + e.getMessage());
            }
        }
    }
}
