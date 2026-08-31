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

    // 1. 查詢所有房間 (GET /api/rooms)
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.findAll();
        return ResponseEntity.ok(rooms);
    }

    // List 查詢：直接回傳陣列，沒資料就是空陣列 []
    @GetMapping("/floor/{floor}")
    public ResponseEntity<List<Room>> getByFloor(@PathVariable Integer floor) {
        return ResponseEntity.ok(roomService.findByFloor(floor));
    }

    // Optional 查詢：若有資料回傳 200 OK，無資料則回傳 404 Not Found
    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<?> getByRoomNumber(@PathVariable String roomNumber) {
        return roomService.findByRoomNumber(roomNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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