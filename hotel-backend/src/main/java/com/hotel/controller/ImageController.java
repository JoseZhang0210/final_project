package com.hotel.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.entity.Image;
import com.hotel.service.ImageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    // 可從 application.properties 讀取自訂路徑，若未設定則自動備用至專案 static/images/room/
    @Value("${file.upload-dir:#{null}}")
    private String customUploadDir;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // 1. 新增 / 上傳圖片 (支援：上傳實體檔案 OR 選擇靜態預設圖片)
    @PostMapping
    public ResponseEntity<?> createImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "staticPath", required = false) String staticPath,
            @RequestParam(value = "imageDesc", required = false) String imageDesc) {
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
            } else if (staticPath != null && !staticPath.trim().isEmpty()) {
                dbPath = staticPath.trim();

                // 情境 C: 兩者皆未提供
            } else {
                return ResponseEntity.badRequest().body("請上傳圖片檔案或選擇預設圖片！");
            }

            // 寫入資料庫
            Image image = new Image();
            image.setPath(dbPath);
            image.setImageDesc(imageDesc != null ? imageDesc : "");

            Image savedImage = imageService.insert(image);
            return new ResponseEntity<>(savedImage, HttpStatus.CREATED);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("圖片處理失敗：" + e.getMessage());
        }
    }

    // 2. 舊版特定上傳端點別名
    @PostMapping("/uploadimagesroompic")
    public ResponseEntity<?> uploadimagesroompic(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "imageDesc", required = false) String imageDesc) {
        return createImage(file, null, imageDesc);
    }

    // 3. 查詢所有圖片
    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        return ResponseEntity.ok(imageService.findAll());
    }

    // 4. 依 ID 查詢單一圖片
    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Integer id) {
        return imageService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 5. 修改圖片資訊
    @PutMapping("/{id}")
    public ResponseEntity<Image> updateImage(@PathVariable Integer id, @RequestBody Image updatedImage) {
        Image image = imageService.update(id, updatedImage);
        if (image != null) {
            return ResponseEntity.ok(image);
        }
        return ResponseEntity.notFound().build();
    }

    // 6. 刪除圖片 (包含資料庫紀錄與實體檔案)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer id) {
        // 先查出圖片資訊以利後面刪除實體檔案 (對接 Optional 回傳)
        Image image = imageService.findById(id).orElse(null);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        boolean deleted = imageService.deleteById(id);
        if (deleted) {
            // 同步刪除實體檔案 (僅針對動態上傳的圖片，避免誤刪內建預設圖片)
            if (image.getPath() != null && image.getPath().startsWith("/images/room/")) {
                String fileName = image.getPath().replace("/images/room/", "");
                Path filePath = Paths.get(getUploadDirPath()).resolve(fileName);
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    System.err.println("刪除實體檔案失敗: " + e.getMessage());
                }
            }
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
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