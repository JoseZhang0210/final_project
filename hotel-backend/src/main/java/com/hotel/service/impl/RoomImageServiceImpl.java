package com.hotel.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.RoomImageDTO;
import com.hotel.model.entity.RoomImage;
import com.hotel.repository.RoomImageRepository;
import com.hotel.service.RoomImageService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomImageServiceImpl implements RoomImageService {

    private final RoomImageRepository imageRepository;

    public RoomImageServiceImpl(RoomImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomImageDTO> findAll() {
        return imageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomImageDTO> findOptionalById(Integer id) {
        return imageRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public RoomImageDTO insert(RoomImageDTO imageDTO) {
        RoomImage image = convertToEntity(imageDTO);
        RoomImage saved = imageRepository.save(image);
        return convertToDTO(saved);
    }

    @Override
    public RoomImageDTO update(Integer id, RoomImageDTO updatedImageDTO) {
        RoomImage existingImage = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的圖片資料"));

        if (updatedImageDTO.getPath() != null) {
            existingImage.setPath(updatedImageDTO.getPath());
        }
        if (updatedImageDTO.getImageDescription() != null) {
            existingImage.setImageDescription(updatedImageDTO.getImageDescription());
        }
        if (updatedImageDTO.getRoomTypeId() != null) {
            existingImage.setRoomTypeId(updatedImageDTO.getRoomTypeId());
        }

        return convertToDTO(existingImage);
    }

    @Override
    public void deleteById(Integer id) {
        if (!imageRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的圖片 ID: " + id + " 不存在");
        }
        imageRepository.deleteById(id);
    }

    private RoomImageDTO convertToDTO(RoomImage image) {
        RoomImageDTO dto = new RoomImageDTO();
        dto.setImageId(image.getImageId());
        dto.setPath(image.getPath());
        dto.setImageDescription(image.getImageDescription());
        dto.setRoomTypeId(image.getRoomTypeId());
        return dto;
    }

    private RoomImage convertToEntity(RoomImageDTO dto) {
        RoomImage image = new RoomImage();
        image.setPath(dto.getPath());
        image.setImageDescription(dto.getImageDescription());
        image.setRoomTypeId(dto.getRoomTypeId());
        return image;
    }
}
