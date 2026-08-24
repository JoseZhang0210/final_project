package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.RoomType;
import com.hotel.repository.RoomTypeRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    // 1. Create - 新增房型
    public RoomType insert(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    // 2. Read All - 取得所有房型
    @Transactional(readOnly = true)
    public List<RoomType> findAll() {
        return roomTypeRepository.findAll();
    }

    // 3-1. Read Optional by ID
    @Transactional(readOnly = true)
    public Optional<RoomType> findOptionalById(Integer id) {
        return roomTypeRepository.findById(id);
    }

    // 3-2. Read by ID (找不到即拋出特定例外)
    @Transactional(readOnly = true)
    public RoomType findById(Integer id) {
        return roomTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的房型資料"));
    }

    // 4. Update - 更新房型資料 (依賴 JPA Dirty Checking)
    public RoomType update(Integer id, RoomType updatedRoomType) {
        RoomType existingRoomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的房型資料"));

        if (updatedRoomType.getTypeName() != null) {
            existingRoomType.setTypeName(updatedRoomType.getTypeName());
        }
        if (updatedRoomType.getBedType() != null) {
            existingRoomType.setBedType(updatedRoomType.getBedType());
        }
        if (updatedRoomType.getRoomDescription() != null) {
            existingRoomType.setRoomDescription(updatedRoomType.getRoomDescription());
        }
        if (updatedRoomType.getPricePerNight() != null) {
            existingRoomType.setPricePerNight(updatedRoomType.getPricePerNight());
        }
        if (updatedRoomType.getCapacity() != null) {
            existingRoomType.setCapacity(updatedRoomType.getCapacity());
        }

        // 交易結束時 JPA 會自動比對狀態並執行 Update SQL，無須 call save()
        return existingRoomType;
    }

    // 5. Delete By Id - 刪除房型
    public void deleteById(Integer id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的房型 ID: " + id + " 不存在");
        }
        roomTypeRepository.deleteById(id);
    }
}