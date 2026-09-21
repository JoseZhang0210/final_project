package com.hotel.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.model.entity.Profile;
import com.hotel.repository.ProfileRepository;

@Service
public class AvatarStorageService {

    private final ProfileRepository profileRepository;
    private final FileStorageService fileStorageService;

    public AvatarStorageService(ProfileRepository profileRepository, FileStorageService fileStorageService) {
        this.profileRepository = profileRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * 儲存頭像圖片檔案並同步更新 Profile 的 avatarUrl
     *
     * @param accountId 對應的帳號 ID
     * @param file      上傳的圖片檔案
     * @return 儲存後的大頭貼 URL 路徑 (例如 /uploads/avatars/xxx.jpg)
     */
    public String storeAvatar(Integer accountId, MultipartFile file) {
        if (accountId == null) {
            throw new IllegalArgumentException("帳號 ID 不得為空");
        }

        String avatarUrl = fileStorageService.storeImage(file, "avatars");

        Profile profile = profileRepository.findByAccountId(accountId).orElse(null);
        if (profile == null) {
            profile = new Profile();
            profile.setAccountId(accountId);
            profile.setCreatedAt(LocalDateTime.now());
        }
        profile.setAvatarUrl(avatarUrl);
        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);

        return avatarUrl;
    }
}
