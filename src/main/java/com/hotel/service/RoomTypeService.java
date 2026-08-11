package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.RoomType;
import com.hotel.repository.RoomTypeRepository;

@Service
@Transactional
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    // Create
    public RoomType insert(RoomType roomType) {
        // 若 imageId 為 0，強制轉為 null，避免外鍵約束失敗
        if (roomType.getImageId() != null && roomType.getImageId() == 0) {
            roomType.setImageId(null);
        }
        return roomTypeRepository.save(roomType);
    }

    // Read All
    @Transactional(readOnly = true)
    public List<RoomType> findAll() {
        return roomTypeRepository.findAll();
    }

    // Read by ID
    @Transactional(readOnly = true)
    public Optional<RoomType> findById(Integer id) {
        return roomTypeRepository.findById(id);
    }

    // Update
    public RoomType update(Integer id, RoomType updatedRoomType) {
        return roomTypeRepository.findById(id)
                .map(roomType -> {
                    if (updatedRoomType.getTypeName() != null) {
                        roomType.setTypeName(updatedRoomType.getTypeName());
                    }
                    if (updatedRoomType.getBedType() != null) {
                        roomType.setBedType(updatedRoomType.getBedType());
                    }
                    if (updatedRoomType.getDescription() != null) {
                        roomType.setDescription(updatedRoomType.getDescription());
                    }
                    if (updatedRoomType.getPricePerNight() != null) {
                        roomType.setPricePerNight(updatedRoomType.getPricePerNight());
                    }
                    if (updatedRoomType.getCapacity() != null) {
                        roomType.setCapacity(updatedRoomType.getCapacity());
                    }
                    
                    // 修正 imageId 邏輯：若是 0 則設為 null，否則更新為新傳入的值
                    if (updatedRoomType.getImageId() != null && updatedRoomType.getImageId() == 0) {
                        roomType.setImageId(null);
                    } else {
                        roomType.setImageId(updatedRoomType.getImageId());
                    }
                    
                    return roomTypeRepository.save(roomType);
                })
                .orElseThrow(() -> new RuntimeException("RoomType not found with id: " + id));
    }

    // Delete By Id
    public boolean deleteById(Integer id) {
        if (roomTypeRepository.existsById(id)) {
            roomTypeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}