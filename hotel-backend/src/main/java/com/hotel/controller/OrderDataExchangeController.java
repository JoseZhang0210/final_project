package com.hotel.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.service.OrderDataExchangeService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 訂單系統資料匯出/匯入 REST Controller。
 * 提供 JSON 備份與還原、CSV 優惠券批次上下傳 API。
 */
@RestController
@RequestMapping("/api/orders/exchange")
@RequiredArgsConstructor
@Slf4j
public class OrderDataExchangeController {

    private final OrderDataExchangeService orderDataExchangeService;

    // =========================================================================
    // 1. 匯出全部訂單、付款、優惠券為 JSON 檔案
    // =========================================================================
    @GetMapping("/export/json")
    public ResponseEntity<byte[]> exportJson() {
        try {
            byte[] bytes = orderDataExchangeService.exportAllToJson();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("order_system_backup.json", StandardCharsets.UTF_8)
                    .build());
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("匯出 JSON 失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("匯出失敗: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }

    // =========================================================================
    // 2. 上傳 JSON 檔案還原 / 匯入訂單系統資料
    // =========================================================================
    @PostMapping(value = "/import/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importJson(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "請選擇要上傳的 JSON 檔案"));
        }
        try {
            int count = orderDataExchangeService.importFromJson(file.getInputStream());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "成功匯入 " + count + " 筆訂單及相關關聯資料！"));
        } catch (Exception e) {
            log.error("匯入 JSON 失敗", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "匯入失敗: " + e.getMessage()));
        }
    }

    // =========================================================================
    // 3. 匯出優惠券清單為 CSV (Excel 友善)
    // =========================================================================
    @GetMapping("/export/coupons/csv")
    public void exportCouponsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"coupons.csv\"");
        orderDataExchangeService.exportCouponsToCsv(response.getWriter());
    }

    // =========================================================================
    // 4. 上傳 CSV 檔案批次匯入優惠券
    // =========================================================================
    @PostMapping(value = "/import/coupons/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importCouponsCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "請選擇要上傳的 CSV 檔案"));
        }
        try {
            int count = orderDataExchangeService.importCouponsFromCsv(file.getInputStream());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "成功批次建立/更新 " + count + " 筆優惠券！"));
        } catch (Exception e) {
            log.error("匯入 CSV 失敗", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "匯入失敗: " + e.getMessage()));
        }
    }

    // =========================================================================
    // 5. 手動觸發種子資料注入 (若為空才注入)
    // =========================================================================
    @PostMapping("/seed")
    public ResponseEntity<Map<String, Object>> triggerSeed() {
        int count = orderDataExchangeService.importFromClasspathSeed();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "種子資料注入作業完成，處理了 " + count + " 筆訂單。"));
    }
}
