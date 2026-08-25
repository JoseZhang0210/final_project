package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.Room;
import com.hotel.repository.RoomRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // 1. Create - 新增房間
    public Room insert(Room room) {
        return roomRepository.save(room);
    }

    // 2. Read All - 取得所有房間
    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    // 1. 依樓層查詢
    @Transactional(readOnly = true)
    public List<Room> findByFloor(Integer floor) {
        return roomRepository.findByFloor(floor);
    }

    // 2. 依房號查詢
    @Transactional(readOnly = true)
    public Optional<Room> findByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber);
    }

    // 4. Update - 更新房間狀態與房型 (利用 Dirty Checking)
    public Room update(Integer id, Room updatedRoom) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的房間資料"));

        // 1. 更新房間狀態
        if (updatedRoom.getRoomStatus() != null) {
            existingRoom.setRoomStatus(updatedRoom.getRoomStatus());
        }

        // 2. 更新房型關聯 (對應 room_type_id)
        if (updatedRoom.getRoomType() != null) {
            existingRoom.setRoomType(updatedRoom.getRoomType());
        }

        // 交易結束時 JPA 會自動比對並發送 UPDATE SQL (僅更新變更之欄位)
        return existingRoom;
    }

    // 5. Delete By Id - 刪除房間
    public void deleteById(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的房間 ID: " + id + " 不存在");
        }
        roomRepository.deleteById(id);
    }
}