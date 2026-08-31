package com.hotel.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductImageController {

        private static final String UPLOAD_DIR = "../hotel-frontend/public/upload/products";

        @PostMapping("/upload-image")
        public ResponseEntity<?> uploadImage(
                        @RequestParam("file") MultipartFile file) {

                try {

                        // ==========================
                        // 沒有檔案
                        // ==========================

                        if (file.isEmpty()) {

                                return ResponseEntity
                                                .badRequest()
                                                .body(
                                                                Map.of(
                                                                                "message",
                                                                                "請選擇圖片"));
                        }

                        // ==========================
                        // 檢查是不是圖片
                        // ==========================

                        String contentType = file.getContentType();

                        if (contentType == null
                                        ||
                                        !contentType
                                                        .startsWith(
                                                                        "image/")) {

                                return ResponseEntity
                                                .badRequest()
                                                .body(
                                                                Map.of(
                                                                                "message",
                                                                                "只能上傳圖片檔案"));
                        }

                        // ==========================
                        // 建立 uploads/products
                        // ==========================

                        Path uploadPath = Paths.get(
                                        UPLOAD_DIR);

                        if (!Files.exists(
                                        uploadPath)) {

                                Files.createDirectories(
                                                uploadPath);
                        }

                        // ==========================
                        // 原始檔名
                        // ==========================

                        String originalName = file.getOriginalFilename();

                        String extension = "";

                        if (originalName != null
                                        &&
                                        originalName.contains(
                                                        ".")) {

                                extension = originalName.substring(
                                                originalName
                                                                .lastIndexOf(
                                                                                "."));
                        }

                        // ==========================
                        // UUID 新檔名
                        // ==========================

                        String filename = UUID
                                        .randomUUID()
                                        .toString()
                                        +
                                        extension;

                        // ==========================
                        // 儲存
                        // ==========================

                        Path targetPath = uploadPath.resolve(
                                        filename);

                        Files.copy(
                                        file.getInputStream(),
                                        targetPath,
                                        StandardCopyOption.REPLACE_EXISTING);

                        // ==========================
                        // 回傳網址
                        // ==========================

                        String imageUrl = "/upload/products/"
                                        +
                                        filename;

                        return ResponseEntity.ok(
                                        Map.of(
                                                        "imageUrl",
                                                        imageUrl));

                } catch (IOException e) {

                        e.printStackTrace();

                        return ResponseEntity
                                        .internalServerError()
                                        .body(
                                                        Map.of(
                                                                        "message",
                                                                        "圖片上傳失敗"));
                }
        }
}