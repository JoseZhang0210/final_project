package com.hotel.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Locale;
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

/**
 * 場地主圖本機一鍵導入 API。
 *
 * 管理者從自己的電腦選擇圖片後：
 *
 * 1. 圖片存入 hotel-backend/uploads/venues。
 * 2. 自動更新 dbo.venue.image_url。
 * 3. 不修改 Venue 其他欄位。
 */
@RestController
@RequestMapping("/api/venues")
public class VenueLocalImageController {

    /** 單張圖片限制 10 MB。 */
    private static final long MAX_IMAGE_BYTES =
            10L * 1024L * 1024L;

    private final JdbcTemplate jdbcTemplate;

    /** 建立使用指定資料庫連線的圖片控制器。 */
    public VenueLocalImageController(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate =
                jdbcTemplate;
    }

    /**
     * 上傳本機圖片並直接設定成場地主圖。
     *
     * POST /api/venues/{id}/upload-image
     */
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadImage(
            @PathVariable Integer id,
            @RequestParam("file")
            MultipartFile file) {

        try {

            /* 場地 ID 必須有效。 */
            if (id == null || id <= 0) {
                return error(
                        HttpStatus.BAD_REQUEST,
                        "場地 ID 不正確");
            }

            /*
             * 直接查 Venue 是否存在。
             * 不透過 JPA Entity，降低圖片上傳與其他 Entity 邏輯互相影響。
             */
            Integer venueCount =
                    jdbcTemplate.queryForObject(
                            """
                            SELECT COUNT(*)
                            FROM dbo.venue
                            WHERE venue_id = ?
                            """,
                            Integer.class,
                            id);

            if (venueCount == null
                    || venueCount == 0) {

                return error(
                        HttpStatus.NOT_FOUND,
                        "找不到指定場地");
            }

            /* 必須真的選擇圖片。 */
            if (file == null
                    || file.isEmpty()) {

                return error(
                        HttpStatus.BAD_REQUEST,
                        "請選擇圖片檔案");
            }

            /* 限制檔案大小。 */
            if (file.getSize()
                    > MAX_IMAGE_BYTES) {

                return error(
                        HttpStatus.BAD_REQUEST,
                        "圖片不可超過 10 MB");
            }

            String contentType =
                    file.getContentType();

            if (contentType == null) {
                return error(
                        HttpStatus.BAD_REQUEST,
                        "無法判斷圖片格式");
            }

            contentType =
                    contentType
                            .trim()
                            .toLowerCase(
                                    Locale.ROOT);

            String extension =
                    getExtension(
                            contentType);

            if (extension == null) {
                return error(
                        HttpStatus.BAD_REQUEST,
                        "只支援 JPG、PNG、WEBP 圖片");
            }

            /*
             * 圖片儲存位置：
             *
             * hotel-backend/uploads/venues
             */
            Path uploadDirectory =
                    Path.of(
                                    "uploads",
                                    "venues")
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(
                    uploadDirectory);

            /*
             * 使用後端自行產生檔名，
             * 不直接採用原始上傳檔名。
             */
            String fileName =
                    "venue-"
                            + id
                            + "-"
                            + System.currentTimeMillis()
                            + "."
                            + extension;

            Path targetFile =
                    uploadDirectory
                            .resolve(fileName)
                            .normalize();

            /*
             * 安全確認檔案仍在
             * uploads/venues 目錄內。
             */
            if (!targetFile.startsWith(
                    uploadDirectory)) {

                return error(
                        HttpStatus.BAD_REQUEST,
                        "圖片儲存路徑不正確");
            }

            /*
             * 將瀏覽器上傳的檔案寫入本機。
             */
            try (
                    var inputStream =
                            file.getInputStream()
            ) {
                Files.copy(
                        inputStream,
                        targetFile,
                        StandardCopyOption
                                .REPLACE_EXISTING);
            }

            String imageUrl =
                    "/uploads/venues/"
                            + fileName;

            /*
             * 只更新 Venue 的 image_url。
             *
             * 不修改：
             * venue_name
             * capacity
             * price_per_day
             * venue_status
             */
            int updatedRows =
                    jdbcTemplate.update(
                            """
                            UPDATE dbo.venue
                            SET image_url = ?
                            WHERE venue_id = ?
                            """,
                            imageUrl,
                            id);

            if (updatedRows != 1) {

                Files.deleteIfExists(
                        targetFile);

                return error(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "場地圖片資料更新失敗");
            }

            /*
             * 使用一般 LinkedHashMap，
             * 不使用不可變 Map.of。
             */
            Map<String, Object> body =
                    new LinkedHashMap<>();

            body.put(
                    "message",
                    "場地主圖已導入成功");

            body.put(
                    "venueId",
                    id);

            body.put(
                    "imageUrl",
                    imageUrl);

            return ResponseEntity.ok(
                    body);

        } catch (IOException e) {

            e.printStackTrace();

            return error(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "圖片檔案儲存失敗");

        } catch (Exception e) {

            /*
             * 目前保留 Stack Trace，
             * 若仍有問題可直接從 PowerShell 看到真正行號。
             */
            System.err.println(
                    "========== Venue 圖片導入錯誤 ==========");

            e.printStackTrace();

            System.err.println(
                    "======================================");

            return error(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "圖片導入失敗："
                            + String.valueOf(
                                    e.getMessage()));
        }
    }

    /**
     * 將 MIME Type 轉換成安全副檔名。
     */
    private String getExtension(
            String contentType) {

        return switch (contentType) {

            case "image/jpeg",
                 "image/jpg" ->
                    "jpg";

            case "image/png" ->
                    "png";

            case "image/webp" ->
                    "webp";

            default ->
                    null;
        };
    }

    /**
     * 建立統一錯誤回應。
     *
     * 使用一般 Map，
     * 避免不可變 Map 的 null lookup 問題。
     */
    private ResponseEntity<Map<String, Object>>
            error(
                    HttpStatus status,
                    String message) {

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put(
                "message",
                message);

        return ResponseEntity
                .status(status)
                .body(body);
    }
}
