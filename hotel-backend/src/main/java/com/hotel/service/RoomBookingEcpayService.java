package com.hotel.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.StringJoiner;

import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hotel.model.dto.BookingDTO;

@Service
public class RoomBookingEcpayService {

    // 綠界測試環境設定 (從 application.properties 注入)
    @Value("${ecpay.stage.url}")
    private String ecpayUrl;

    @Value("${ecpay.stage.merchant-id}")
    private String merchantId;

    @Value("${ecpay.stage.hash-key}")
    private String hashKey;

    @Value("${ecpay.stage.hash-iv}")
    private String hashIv;

    public String genAioCheckOutHTML(BookingDTO booking, String returnUrl, String clientBackUrl) {
        // 1. 建立依照字母不區分大小寫排序的 TreeMap
        Map<String, String> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        params.put("MerchantID", merchantId);
        String tradeNo = "HOTEL" + booking.getBookingId() + "T" + (System.currentTimeMillis() % 10000);
        params.put("MerchantTradeNo", tradeNo);
        params.put("MerchantTradeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(booking.getBookingPrice()));
        params.put("TradeDesc", "Hotel Room Booking");
        params.put("ItemName", "訂單編號: " + booking.getBookingId() + " 住宿費用");
        params.put("ReturnURL", returnUrl); 
        params.put("ClientBackURL", clientBackUrl); 
        params.put("ChoosePayment", "Credit"); // 強制指定信用卡以利展示
        params.put("EncryptType", "1"); // SHA256

        // 2. 產生 CheckMacValue
        try {
            String checkMacValue = generateCheckMacValue(params);
            params.put("CheckMacValue", checkMacValue);
        } catch (Exception e) {
            throw new RuntimeException("CheckMacValue generation failed", e);
        }

        // 3. 組合 HTML 表單字串
        StringBuilder html = new StringBuilder();
        html.append("<form id='ecpay-form' action='").append(ecpayUrl).append("' method='POST'>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            html.append("<input type='hidden' name='").append(entry.getKey()).append("' value='")
                    .append(entry.getValue()).append("' />");
        }
        html.append("<script>document.getElementById('ecpay-form').submit();</script>");
        html.append("</form>");

        return html.toString();
    }

    public boolean verifyCheckMacValue(Map<String, String> params) {
        String receivedMac = params.get("CheckMacValue");
        if (receivedMac == null) return false;

        Map<String, String> checkParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!"CheckMacValue".equalsIgnoreCase(entry.getKey())) {
                checkParams.put(entry.getKey(), entry.getValue());
            }
        }

        try {
            String calculatedMac = generateCheckMacValue(checkParams);
            return calculatedMac.equals(receivedMac);
        } catch (Exception e) {
            return false;
        }
    }

    private String generateCheckMacValue(Map<String, String> params) throws Exception {
        StringJoiner sj = new StringJoiner("&");
        params.forEach((k, v) -> sj.add(k + "=" + v));
        String raw = "HashKey=" + hashKey + "&" + sj + "&HashIV=" + hashIv;
        String encoded = ecpayUrlEncode(raw);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(encoded.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }

    public static String ecpayUrlEncode(String source) throws Exception {
        String encoded = URLEncoder.encode(source, StandardCharsets.UTF_8.name());
        encoded = encoded.toLowerCase();
        return encoded
            .replace("%2d", "-").replace("%5f", "_").replace("%2e", ".")
            .replace("%21", "!").replace("%2a", "*")
            .replace("%28", "(").replace("%29", ")")
            .replace("~", "%7e"); 
    }
}
