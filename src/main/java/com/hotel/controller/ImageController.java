package com.hotel.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.entity.Image;
import com.hotel.service.ImageService;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    // 專案原始碼目錄
    private static final String UPLOAD_DIR_SRC = "src/main/resources/static/images/room/";

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // 1. 新增圖片 (上傳實體檔案 + 寫入 SQL)
    @PostMapping
    public ResponseEntity<?> createImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("imageDesc") String imageDesc) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("請選擇圖片檔案！");
            }

            // 確保資料夾存在
            File dir = new File(UPLOAD_DIR_SRC);
            if (!dir.exists()) dir.mkdirs();

            // 產生唯一檔名避免重複
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetPath = Paths.get(UPLOAD_DIR_SRC + fileName);

            // 將檔案複製入 static/images/room/
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 設定寫入 SQL 的網址路徑
            String dbPath = "/images/room/" + fileName;

            Image image = new Image();
            image.setPath(dbPath);
            image.setImageDesc(imageDesc);

            Image savedImage = imageService.insert(image);
            return new ResponseEntity<>(savedImage, HttpStatus.CREATED);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上傳失敗：" + e.getMessage());
        }
    }

    // 2. 查詢所有圖片 (供下拉選單或列表使用)
    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        return ResponseEntity.ok(imageService.findAll());
    }

    // 3. 刪除圖片
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer id) {
        if (imageService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}