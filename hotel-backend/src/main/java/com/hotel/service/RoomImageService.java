package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.RoomImage;
import com.hotel.repository.RoomImageRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomImageService {

    private final RoomImageRepository imageRepository;

    public RoomImageService(RoomImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    // 1. Create - 新增圖片
    public RoomImage insert(RoomImage image) {
        return imageRepository.save(image);
    }

    // 2. Read All - 查詢所有圖片
    @Transactional(readOnly = true)
    public List<RoomImage> findAll() {
        return imageRepository.findAll();
    }

    // 3-1. Read Optional by ID (保留彈性)
    @Transactional(readOnly = true)
    public Optional<RoomImage> findOptionalById(Integer id) {
        return imageRepository.findById(id);
    }

    // // 3-2. Read by ID (找不到即拋出特定例外)
    // @Transactional(readOnly = true)
    // public RoomImage findById(Integer id) {
    // return imageRepository.findById(id)
    // .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的圖片資料"));
    // }

    // 4. Update - 修改圖片資訊 (利用 Dirty Checking 自動更新)
    public RoomImage update(Integer id, RoomImage updatedImage) {
        RoomImage existingImage = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的圖片資料"));

        if (updatedImage.getPath() != null) {
            existingImage.setPath(updatedImage.getPath());
        }
        // 修正 getter/setter 欄位名稱 (原為 getImageDesc)
        if (updatedImage.getImageDescription() != null) {
            existingImage.setImageDescription(updatedImage.getImageDescription());
        }
        if (updatedImage.getRoomType() != null) {
            existingImage.setRoomType(updatedImage.getRoomType());
        }

        // 交易結束時 JPA 會自動比對並發送 Update SQL，無需 call save()
        return existingImage;
    }

    // 5. Delete - 刪除圖片
    public void deleteById(Integer id) {
        if (!imageRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的圖片 ID: " + id + " 不存在");
        }
        imageRepository.deleteById(id);
    }

    public RoomImage findById(Integer id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}