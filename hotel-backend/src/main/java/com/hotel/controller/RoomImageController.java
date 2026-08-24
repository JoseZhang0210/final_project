package com.hotel.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.model.entity.RoomImage;
import com.hotel.service.RoomImageService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/images")
public class RoomImageController {

    private final RoomImageService imageService;

    // 可從 application.properties 讀取自訂路徑，若未設定則自動備用至專案 uploads 目錄
    @Value("${file.upload-dir:#{null}}")
    private String customUploadDir;

    public RoomImageController(RoomImageService imageService) {
        this.imageService = imageService;
    }

    // 1. 新增 / 上傳圖片 (支援：上傳實體檔案 OR 選擇靜態預設圖片)
    @PostMapping
    public ResponseEntity<?> createImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "staticPath", required = false) String staticPath,
            @RequestParam(value = "imageDescription", required = false) String imageDescription) {
        try {
            String dbPath = "";

            // 情境 A: 有上傳新實體檔案
            if (file != null && !file.isEmpty()) {
                String uploadDirPath = getUploadDirPath();
                File dir = new File(uploadDirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // 清理檔案名稱以防路徑穿越攻擊 (Path Traversal)
                String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
                Path targetPath = Paths.get(uploadDirPath).resolve(fileName);

                // 寫入檔案
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Web 端讀取路徑
                dbPath = "/images/room/" + fileName;

                // 情境 B: 選擇系統內建的靜態圖片
            } else if (StringUtils.hasText(staticPath)) {
                dbPath = staticPath.trim();

                // 情境 C: 兩者皆未提供
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "請上傳圖片檔案或選擇預設圖片！"));
            }

            // 寫入資料庫
            RoomImage image = new RoomImage();
            image.setPath(dbPath);
            image.setImageDescription(imageDescription != null ? imageDescription : "");

            RoomImage savedImage = imageService.insert(image);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedImage);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "圖片處理失敗：" + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "新增圖片失敗：" + e.getMessage()));
        }
    }

    // 2. 舊版特定上傳端點別名
    @PostMapping("/uploadimagesroompic")
    public ResponseEntity<?> uploadimagesroompic(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "imageDescription", required = false) String imageDescription) {
        return createImage(file, null, imageDescription);
    }

    // 3. 查詢所有圖片
    @GetMapping
    public ResponseEntity<List<RoomImage>> getAllImages() {
        return ResponseEntity.ok(imageService.findAll());
    }

    // 4. 依 ID 查詢單一圖片
    @GetMapping("/{id}")
    public ResponseEntity<?> getImageById(@PathVariable Integer id) {
        try {
            RoomImage image = imageService.findById(id);
            return ResponseEntity.ok(image);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // 5. 修改圖片資訊
    @PutMapping("/{id}")
    public ResponseEntity<?> updateImage(@PathVariable Integer id, @RequestBody RoomImage updatedImage) {
        try {
            RoomImage image = imageService.update(id, updatedImage);
            return ResponseEntity.ok(image);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "更新失敗：" + e.getMessage()));
        }
    }

    // 6. 刪除圖片 (包含資料庫紀錄與實體檔案)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer id) {
        try {
            // 先查出圖片實體路徑，以便後續刪除檔案
            RoomImage image = imageService.findById(id);

            // 執行 Service 刪除（若不存在會丟出 EntityNotFoundException）
            imageService.deleteById(id);

            // 同步刪除本地實體檔案 (僅針對上傳檔案，避免誤刪靜態資源)
            if (image.getPath() != null && image.getPath().startsWith("/images/room/")) {
                String fileName = image.getPath().replace("/images/room/", "");
                Path filePath = Paths.get(getUploadDirPath()).resolve(fileName);
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    System.err.println("資料庫已刪除，但實體檔案刪除失敗: " + e.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of("message", "圖片刪除成功！"));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "刪除失敗：該圖片可能已被其他資料關聯！"));
        }
    }

    // 取得圖片上傳實體路徑 Helper
    private String getUploadDirPath() {
        if (StringUtils.hasText(customUploadDir)) {
            return customUploadDir;
        }
        return System.getProperty("user.dir")
                + File.separator + "uploads"
                + File.separator + "images"
                + File.separator + "room"
                + File.separator;
    }
}