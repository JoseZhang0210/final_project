package com.hotel.controller;

import java.util.List;
import java.util.Map;

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
import com.hotel.service.FileStorageService;
import com.hotel.service.RoomImageService;

@RestController
@RequestMapping("/api/images")
public class RoomImageController {

    private final RoomImageService imageService;
    private final FileStorageService fileStorageService;

    public RoomImageController(RoomImageService imageService, FileStorageService fileStorageService) {
        this.imageService = imageService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<RoomImageDTO> createImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "staticPath", required = false) String staticPath,
            @RequestParam(value = "imageDescription", required = false) String imageDescription,
            @RequestParam(value = "roomTypeId", required = false) Integer roomTypeId) {

        String dbPath;

        if (file != null && !file.isEmpty()) {
            dbPath = fileStorageService.storeImage(file, "images/room");
        } else if (StringUtils.hasText(staticPath)) {
            dbPath = staticPath.trim();
        } else {
            throw new IllegalArgumentException("請上傳圖片檔案或選擇預設圖片！");
        }

        RoomImageDTO imageDTO = new RoomImageDTO();
        imageDTO.setPath(dbPath);
        imageDTO.setImageDescription(imageDescription != null ? imageDescription : "");
        imageDTO.setRoomTypeId(roomTypeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.insert(imageDTO));
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
        RoomImageDTO imageDTO = imageService.findOptionalById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("找不到 ID 為 " + id + " 的圖片資料"));

        imageService.deleteById(id);

        if (imageDTO.getPath() != null && imageDTO.getPath().startsWith("/uploads/")) {
            fileStorageService.deleteFile(imageDTO.getPath());
        }

        return ResponseEntity.ok(Map.of("message", "圖片刪除成功！"));
    }
}