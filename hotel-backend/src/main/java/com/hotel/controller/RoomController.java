package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Room;
import com.hotel.service.RoomService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // 1. 新增房間 (POST /api/rooms)
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        try {
            // 確保新增時主鍵為 null，由資料庫自動遞增
            room.setRoomId(null);
            Room savedRoom = roomService.insert(room);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("FK_")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "外鍵約束錯誤：請確認關聯的房型 ID 是否正確！"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "新增失敗，請檢查欄位格式！"));
        }
    }

    // 2. 查詢所有房間 / 依條件過濾 (GET /api/rooms)
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms(
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Integer roomTypeId) {

        List<Room> rooms;

        if (floor != null) {
            rooms = roomService.findByFloor(floor);
        } else if (roomTypeId != null) {
            rooms = roomService.findByRoomTypeId(roomTypeId);
        } else {
            rooms = roomService.findAll();
        }

        return ResponseEntity.ok(rooms);
    }

    // 3. 依 ID 查詢單一房間 (GET /api/rooms/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Integer id) {
        try {
            Room room = roomService.findById(id);
            return ResponseEntity.ok(room);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "系統錯誤：" + e.getMessage()));
        }
    }

    // 4. 修改房間資訊 (PUT /api/rooms/{id})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Integer id, @RequestBody Room roomDetails) {
        try {
            Room updatedRoom = roomService.update(id, roomDetails);
            return ResponseEntity.ok(updatedRoom);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "更新失敗：" + e.getMessage()));
        }
    }

    // 5. 刪除房間 (DELETE /api/rooms/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Integer id) {
        try {
            roomService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "房間刪除成功！"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "刪除失敗：該房間可能已被訂單或其他資料關聯！"));
        }
    }
}