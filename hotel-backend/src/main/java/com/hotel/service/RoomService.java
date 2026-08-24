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

    // 2-1. Read by Floor - 依樓層查詢
    @Transactional(readOnly = true)
    public List<Room> findByFloor(Integer floor) {
        return roomRepository.findByFloor(floor);
    }

    // 2-2. Read by RoomTypeId - 依房型 ID 查詢
    @Transactional(readOnly = true)
    public List<Room> findByRoomTypeId(Integer roomTypeId) {
        return roomRepository.findByRoomType_RoomTypeId(roomTypeId);
    }

    // 3-1. Read Optional by ID
    @Transactional(readOnly = true)
    public Optional<Room> findOptionalById(Integer id) {
        return roomRepository.findById(id);
    }

    // 3-2. Read by ID (找不到時拋出例外)
    @Transactional(readOnly = true)
    public Room findById(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的房間資料"));
    }

    // 4. Update - 更新房間資料 (利用 Dirty Checking)
    public Room update(Integer id, Room updatedRoom) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的房間資料"));

        if (updatedRoom.getRoomNumber() != null) {
            existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        }
        if (updatedRoom.getFloor() != null) {
            existingRoom.setFloor(updatedRoom.getFloor());
        }
        if (updatedRoom.getRoomStatus() != null) {
            existingRoom.setRoomStatus(updatedRoom.getRoomStatus());
        }
        // 修正：依據 Entity 的關聯設定更新 RoomType 物件，而非不存在的 roomTypeId 屬性
        if (updatedRoom.getRoomType() != null) {
            existingRoom.setRoomType(updatedRoom.getRoomType());
        }

        // 交易結束時 JPA 會自動進行比對並發送 Update SQL，無需 call save()
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