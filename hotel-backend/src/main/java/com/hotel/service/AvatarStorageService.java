package com.hotel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.model.entity.Profile;
import com.hotel.repository.ProfileRepository;

@Service
public class AvatarStorageService {

    private final ProfileRepository profileRepository;

    public AvatarStorageService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * 儲存頭像圖片檔案並同步更新 Profile 的 avatarUrl (統一封裝檔案檢驗與存放邏輯 - DRY)
     *
     * @param accountId 對應的帳號 ID
     * @param file      上傳的圖片檔案
     * @return 儲存後的大頭貼 URL 路徑 (例如 /uploads/avatars/avatar-1-123456789.jpg)
     */
    public String storeAvatar(Integer accountId, MultipartFile file) {
        if (accountId == null) {
            throw new IllegalArgumentException("帳號 ID 不得為空");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("請選擇要上傳的頭像圖片");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("圖片檔案大小不能超過 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("請上傳有效的圖片檔案 (JPG, PNG, WEBP, GIF 等)");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }

        String fileName = "avatar-" + accountId + "-" + System.currentTimeMillis() + "." + ext;

        try {
            Path uploadDir = Paths.get("uploads", "avatars").toAbsolutePath().normalize();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Profile profile = profileRepository.findByAccountId(accountId).orElse(null);
            if (profile == null) {
                profile = new Profile();
                profile.setAccountId(accountId);
                profile.setCreatedAt(LocalDateTime.now());
            }
            String avatarUrl = "/uploads/avatars/" + fileName;
            profile.setAvatarUrl(avatarUrl);
            profile.setUpdatedAt(LocalDateTime.now());
            profileRepository.save(profile);

            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("頭像圖片儲存失敗：" + e.getMessage(), e);
        }
    }
}

