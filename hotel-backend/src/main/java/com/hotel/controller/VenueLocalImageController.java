package com.hotel.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.service.FileStorageService;

/**
 * 場地主圖本機一鍵導入 API
 */
@RestController
@RequestMapping("/api/venues")
public class VenueLocalImageController {

    private final JdbcTemplate jdbcTemplate;
    private final FileStorageService fileStorageService;

    public VenueLocalImageController(JdbcTemplate jdbcTemplate, FileStorageService fileStorageService) {
        this.jdbcTemplate = jdbcTemplate;
        this.fileStorageService = fileStorageService;
    }

    /**
     * 上傳本機圖片並直接設定成場地主圖。
     * POST /api/venues/{id}/upload-image
     */
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadImage(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {

        if (id == null || id <= 0) {
            return error(HttpStatus.BAD_REQUEST, "場地 ID 不正確");
        }

        Integer venueCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dbo.venue WHERE venue_id = ?",
                Integer.class,
                id);

        if (venueCount == null || venueCount == 0) {
            return error(HttpStatus.NOT_FOUND, "找不到指定場地");
        }

        try {
            String imageUrl = fileStorageService.storeImage(file, "venues");

            int updatedRows = jdbcTemplate.update(
                    "UPDATE dbo.venue SET image_url = ? WHERE venue_id = ?",
                    imageUrl,
                    id);

            if (updatedRows != 1) {
                fileStorageService.deleteFile(imageUrl);
                return error(HttpStatus.INTERNAL_SERVER_ERROR, "場地圖片資料更新失敗");
            }

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("message", "場地主圖已導入成功");
            body.put("venueId", id);
            body.put("imageUrl", imageUrl);

            return ResponseEntity.ok(body);

        } catch (IllegalArgumentException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "圖片導入失敗：" + e.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
