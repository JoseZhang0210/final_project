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
            @RequestParam(value = "imageDescription", required = false) String imageDescription) throws IOException {
        
        String dbPath = "";

        if (file != null && !file.isEmpty()) {
            String uploadDirPath = getUploadDirPath();
            File dir = new File(uploadDirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path targetPath = Paths.get(uploadDirPath).resolve(fileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            dbPath = "/images/room/" + fileName;

        } else if (StringUtils.hasText(staticPath)) {
            dbPath = staticPath.trim();
        } else {
            throw new IllegalArgumentException("請上傳圖片檔案或選擇預設圖片！");
        }

        RoomImageDTO imageDTO = new RoomImageDTO();
        imageDTO.setPath(dbPath);
        imageDTO.setImageDescription(imageDescription != null ? imageDescription : "");

        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.insert(imageDTO));
    }

    @PostMapping("/uploadimagesroompic")
    public ResponseEntity<RoomImageDTO> uploadimagesroompic(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "imageDescription", required = false) String imageDescription) throws IOException {
        return createImage(file, null, imageDescription);
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
    public ResponseEntity<RoomImageDTO> updateImage(@PathVariable Integer id, @RequestBody RoomImageDTO updatedImageDTO) {
        return ResponseEntity.ok(imageService.update(id, updatedImageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable Integer id) {
        RoomImageDTO imageDTO = imageService.findOptionalById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("找不到 ID 為 " + id + " 的圖片資料"));

        imageService.deleteById(id);

        if (imageDTO.getPath() != null && imageDTO.getPath().startsWith("/images/room/")) {
            String fileName = imageDTO.getPath().replace("/images/room/", "");
            Path filePath = Paths.get(getUploadDirPath()).resolve(fileName);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("資料庫已刪除，但實體檔案刪除失敗: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(Map.of("message", "圖片刪除成功！"));
    }

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