package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.Image;
import com.hotel.repository.ImageRepository;

@Service
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    // 1. Create - 新增圖片
    public Image insert(Image image) {
        return imageRepository.save(image);
    }

    // 2. Read All - 查詢所有圖片
    @Transactional(readOnly = true)
    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    // 3. Read by ID - 依 ID 查詢圖片
    @Transactional(readOnly = true)
    public Optional<Image> findById(Integer id) {
        return imageRepository.findById(id);
    }

    // 4. Update - 修改圖片資訊
    public Image update(Integer id, Image updatedImage) {
        Image existingImage = imageRepository.findById(id).orElse(null);
        if (existingImage == null) {
            return null;
        }

        if (updatedImage.getPath() != null) {
            existingImage.setPath(updatedImage.getPath());
        }
        if (updatedImage.getImageDesc() != null) {
            existingImage.setImageDesc(updatedImage.getImageDesc());
        }

        return imageRepository.save(existingImage);
    }

    // 5. Delete - 刪除圖片
    public boolean deleteById(Integer id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
            return true;
        }
        return false;
    }
}