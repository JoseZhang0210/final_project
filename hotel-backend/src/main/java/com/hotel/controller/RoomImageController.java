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

import com.hotel.model.dto.RoomImageDTO;
import com.hotel.service.RoomImageService;

@RestController
@RequestMapping("/api/images")
public class RoomImageController {

    private final RoomImageService imageService;

    @Value("${file.upload-dir:#{null}}")
    private String customUploadDir;

    public RoomImageController(RoomImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity<RoomImageDTO> createImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "staticPath", required = false) String staticPath,
            @RequestParam(value = "imageDescription", required = false) String imageDescription,
            @RequestParam(value = "roomTypeId", required = false) Integer roomTypeId) throws IOException {

        String dbPath = "";

        // 判斷是否有上傳實體檔案
        if (file != null && !file.isEmpty()) {
            String uploadDirPath = getUploadDirPath();
            File dir = new File(uploadDirPath);

            // 若資料夾不存在，則自動建立多層目錄
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 清理檔名並加上 UUID 防止檔名衝突與目錄遍歷安全性問題
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path targetPath = Paths.get(uploadDirPath).resolve(fileName);

            // 將上傳的檔案複製到目標路徑，若存在則覆蓋
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 設定存入資料庫的相對路徑 (用於 URL 存取)
            dbPath = "/uploads/images/room/" + fileName;

        } else if (StringUtils.hasText(staticPath)) {
            // 若沒有上傳實體檔案，但有提供現成的靜態路徑 (例如 Unsplash 網址)，則直接使用
            dbPath = staticPath.trim();
        } else {
            throw new IllegalArgumentException("請上傳圖片檔案或選擇預設圖片！");
        }

        // 建立 DTO 並寫入資料庫
        RoomImageDTO imageDTO = new RoomImageDTO();
        imageDTO.setPath(dbPath);
        imageDTO.setImageDescription(imageDescription != null ? imageDescription : "");
        imageDTO.setRoomTypeId(roomTypeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.insert(imageDTO));
    }

    @PostMapping("/uploadimagesroompic")
    public ResponseEntity<RoomImageDTO> uploadImagesRoomPic(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "imageDescription", required = false) String imageDescription) throws IOException {
        return createImage(file, null, imageDescription, null);
    }

    @GetMapping
    public ResponseEntity<List<RoomImageDTO>> getAllImages() {
        return ResponseEntity.ok(imageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomImageDTO> getImageById(@PathVariable Integer id) {
        return imageService.findOptionalById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("找不到 ID 為 " + id + " 的圖片資料"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomImageDTO> updateImage(@PathVariable Integer id,
            @RequestBody RoomImageDTO updatedImageDTO) {
        return ResponseEntity.ok(imageService.update(id, updatedImageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable Integer id) {
        // 先確認資料庫中是否存在該圖片紀錄
        RoomImageDTO imageDTO = imageService.findOptionalById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("找不到 ID 為 " + id + " 的圖片資料"));

        // 1. 刪除資料庫紀錄
        imageService.deleteById(id);

        // 2. 刪除本機實體檔案 (僅針對存在 /uploads/images/room/ 目錄下的自訂圖片)
        if (imageDTO.getPath() != null && imageDTO.getPath().startsWith("/uploads/images/room/")) {
            String fileName = imageDTO.getPath().replace("/uploads/images/room/", "");
            Path filePath = Paths.get(getUploadDirPath()).resolve(fileName);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // 即使實體檔案刪除失敗 (例如檔案被鎖定或已被手動刪除)，也不影響 API 回傳成功，因為資料庫已被成功刪除
                System.err.println("資料庫已刪除，但實體檔案刪除失敗: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(Map.of("message", "圖片刪除成功！"));
    }

    /**
     * 取得上傳檔案的本機絕對路徑
     * 使用 System.getProperty("user.home") 確保在 MacBook、Linux、Windows 上都有統一的儲存基準路徑，
     * 不會因為應用程式在哪裡啟動而改變。
     * 
     * 📍 實際儲存路徑範例：
     * 1. MacBook:
     * /Users/vic/hotel_uploads/images/room/
     * 
     * 2. Linux:
     * /home/你的帳號/hotel_uploads/images/room/
     * 
     * 3. Windows:
     * C:\\Users\\你的帳號\\hotel_uploads\\images\\room\\
     */
    private String getUploadDirPath() {
        // 優先使用 application.properties 設定的路徑
        if (StringUtils.hasText(customUploadDir)) {
            return customUploadDir;
        }
        // 若未設定，則使用當前使用者的家目錄，確保各作業系統與啟動環境一致
        return System.getProperty("user.home")
                + File.separator + "uploads"
                + File.separator + "images"
                + File.separator + "room"
                + File.separator;
    }
}