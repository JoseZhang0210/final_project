package com.hotel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10MB
    private static final Map<String, String> ALLOWED_IMAGE_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/jpg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif"
    );

    /**
     * 儲存圖片檔案至 uploads/{subDir} 並回傳可存取的相對 URL
     *
     * @param file   前端上傳的 MultipartFile
     * @param subDir 子目錄名稱 (例如 "avatars", "products", "images/room", "venues")
     * @return 存取路徑 (例如 "/uploads/avatars/xxx.jpg")
     */
    public String storeImage(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("請選擇圖片檔案");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("圖片檔案大小不能超過 10MB");
        }

        String contentType = file.getContentType();
        String normalizedContentType = contentType == null ? "" : contentType.trim().toLowerCase(Locale.ROOT);
        String extension = ALLOWED_IMAGE_TYPES.get(normalizedContentType);

        if (extension == null) {
            String originalName = file.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase(Locale.ROOT);
                if (ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || ext.equals(".webp") || ext.equals(".gif")) {
                    extension = ext.equals(".jpeg") ? ".jpg" : ext;
                }
            }
        }

        if (extension == null) {
            throw new IllegalArgumentException("僅支援 JPG、PNG、WEBP、GIF 格式圖片");
        }

        String cleanSubDir = (subDir == null || subDir.isBlank()) ? "misc" : subDir.replaceAll("^[\\\\/]+|[\\\\/]+$", "");
        String filename = UUID.randomUUID().toString() + extension;

        try {
            Path targetDir = resolveUploadDir(cleanSubDir);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            Path targetPath = targetDir.resolve(filename).normalize();
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + cleanSubDir.replace("\\", "/") + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("圖片儲存失敗：" + e.getMessage(), e);
        }
    }

    /**
     * 刪除指定 /uploads/... 相對路徑的實體檔案
     *
     * @param relativeUrl 例如 "/uploads/images/room/xxx.jpg"
     */
    public void deleteFile(String relativeUrl) {
        if (relativeUrl == null || !relativeUrl.startsWith("/uploads/")) {
            return;
        }
        try {
            String subPath = relativeUrl.substring("/uploads/".length());
            Files.deleteIfExists(Paths.get("uploads").resolve(subPath).toAbsolutePath().normalize());
            if (Files.exists(Paths.get("..", "uploads"))) {
                Files.deleteIfExists(Paths.get("..", "uploads").resolve(subPath).toAbsolutePath().normalize());
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 自動判定適當的實體上傳根目錄位置 (支援從根目錄或 hotel-backend 內啟動)
     */
    private Path resolveUploadDir(String cleanSubDir) {
        if (!Files.exists(Paths.get("uploads")) && Files.exists(Paths.get("..", "uploads"))) {
            return Paths.get("..", "uploads", cleanSubDir).toAbsolutePath().normalize();
        }
        return Paths.get("uploads", cleanSubDir).toAbsolutePath().normalize();
    }
}
