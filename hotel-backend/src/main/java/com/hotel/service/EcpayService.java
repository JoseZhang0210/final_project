package com.hotel.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.hotel.model.dto.BookingDTO;

@Service
public class EcpayService {

    // 綠界測試環境設定
    private static final String ECPAY_URL = "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";
    private static final String MERCHANT_ID = "3002607"; // 綠界官方公用測試帳號之一
    private static final String HASH_KEY = "pwFHCqoQZGmho4w6";
    private static final String HASH_IV = "EkRm7iFT261dpeeg";

    // 如果您使用自己的商店代號，請替換為您專屬的 HashKey / HashIV
    // private static final String MERCHANT_ID = "2000132";
    // private static final String HASH_KEY = "5294y06JbISpM5x9";
    // private static final String HASH_IV = "v77hoKGq4kWxNNIS";

    public String genAioCheckOutHTML(BookingDTO booking, String returnUrl, String clientBackUrl) {
        // 1. 建立依照字母排序的 TreeMap
        Map<String, String> params = new TreeMap<>();

        // 基本參數
        params.put("MerchantID", MERCHANT_ID);
        // MerchantTradeNo 必須唯一，這裡用 HOTEL + bookingId + Timestamp
        String tradeNo = "HOTEL" + booking.getBookingId() + "T" + (System.currentTimeMillis() % 10000);
        params.put("MerchantTradeNo", tradeNo);
        params.put("MerchantTradeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(booking.getBookingPrice()));
        params.put("TradeDesc", "Hotel Room Booking");
        params.put("ItemName", "訂單編號: " + booking.getBookingId() + " 住宿費用");
        params.put("ReturnURL", returnUrl); // 背景回傳 (必須是外網 HTTPS)
        params.put("ClientBackURL", clientBackUrl); // 前景返回 (可以是 localhost)
        params.put("ChoosePayment", "Credit"); // 預設信用卡
        params.put("EncryptType", "1"); // SHA256

        // 2. 產生 CheckMacValue
        String checkMacValue = generateCheckMacValue(params);
        params.put("CheckMacValue", checkMacValue);

        // 3. 組合 HTML 表單字串
        StringBuilder html = new StringBuilder();
        html.append("<form id='ecpay-form' action='").append(ECPAY_URL).append("' method='POST'>");
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
        if (receivedMac == null)
            return false;

        // 複製一份 Map 排除 CheckMacValue 本身來重算
        Map<String, String> checkParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!"CheckMacValue".equals(entry.getKey())) {
                checkParams.put(entry.getKey(), entry.getValue());
            }
        }

        String calculatedMac = generateCheckMacValue(checkParams);
        return calculatedMac.equals(receivedMac);
    }

    private String generateCheckMacValue(Map<String, String> params) {
        // 1. 將參數組合成 Query String
        String raw = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        // 2. 前後加上 HashKey 和 HashIV
        raw = "HashKey=" + HASH_KEY + "&" + raw + "&HashIV=" + HASH_IV;

        // 3. URL Encode
        String encoded = urlEncode(raw).toLowerCase();

        // 4. SHA256 加密後轉大寫
        return DigestUtils.sha256Hex(encoded).toUpperCase();
    }

    private String urlEncode(String data) {
        try {
            // Java 內建的 URLEncoder 某些字元編碼結果與綠界不同，需做替換
            // 參考綠界規格書：特殊字元需還原
            return URLEncoder.encode(data, StandardCharsets.UTF_8.name())
                    .replace("%21", "!")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%2A", "*")
                    .replace("%7E", "~")
                    .replace("+", "%20");
        } catch (Exception e) {
            throw new RuntimeException("URL Encode Failed", e);
        }
    }
}
