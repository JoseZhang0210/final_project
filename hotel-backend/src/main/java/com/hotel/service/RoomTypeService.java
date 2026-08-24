package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.RoomType;
import com.hotel.repository.RoomTypeRepository;

@Service
@Transactional
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    // 1. Create - 新增房型
    @Transactional
    public RoomType insert(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    // 2. Read All - 取得所有房型
    @Transactional(readOnly = true)
    public List<RoomType> findAll() {
        return roomTypeRepository.findAll();
    }

    // 3-1. Read Optional by ID (保留原本的方法以利其他彈性用途)
    @Transactional(readOnly = true)
    public Optional<RoomType> findOptionalById(Integer id) {
        return roomTypeRepository.findById(id);
    }

    // 3-2. Read by ID (可以直接回傳 Entity 物件，完美對接 REST Controller)
    @Transactional(readOnly = true)
    public RoomType findById(Integer id) {
        return roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的房型資料"));
    }

    // 4. Update - 更新房型資料
    public RoomType update(Integer id, RoomType updatedRoomType) {
        return roomTypeRepository.findById(id)
                .map(roomType -> {
                    if (updatedRoomType.getTypeName() != null) {
                        roomType.setTypeName(updatedRoomType.getTypeName());
                    }
                    if (updatedRoomType.getBedType() != null) {
                        roomType.setBedType(updatedRoomType.getBedType());
                    }
                    if (updatedRoomType.getRoomDescription() != null) {
                        roomType.setRoomDescription(updatedRoomType.getRoomDescription());
                    }
                    if (updatedRoomType.getPricePerNight() != null) {
                        roomType.setPricePerNight(updatedRoomType.getPricePerNight());
                    }
                    if (updatedRoomType.getCapacity() != null) {
                        roomType.setCapacity(updatedRoomType.getCapacity());
                    }

                    return roomTypeRepository.save(roomType);
                })
                .orElseThrow(() -> new RuntimeException(" RoomType not found with id: " +
                        id));
    }

    // 5. Delete By Id - 刪除房型
    public boolean deleteById(Integer id) {
        if (roomTypeRepository.existsById(id)) {
            roomTypeRepository.deleteById(id);
            return true;
        }
        return false;
    }

}