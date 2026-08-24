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

import com.hotel.model.entity.RoomType;
import com.hotel.service.RoomTypeService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/roomtypes")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    // 1. 取得所有房型列表 (GET /api/roomtypes)
    @GetMapping
    public ResponseEntity<List<RoomType>> getAllRoomTypes() {
        List<RoomType> list = roomTypeService.findAll();
        return ResponseEntity.ok(list);
    }

    // 2. 取得單筆房型資料 (GET /api/roomtypes/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomTypeById(@PathVariable Integer id) {
        try {
            RoomType roomType = roomTypeService.findById(id);
            return ResponseEntity.ok(roomType);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "系統錯誤：" + e.getMessage()));
        }
    }

    // 3. 新增房型 (POST /api/roomtypes)
    @PostMapping
    public ResponseEntity<?> createRoomType(@RequestBody RoomType roomType) {
        try {
            // 確保新增時不傳入指定 ID（由 DB 自增生成）
            roomType.setRoomTypeId(null);
            RoomType savedRoomType = roomTypeService.insert(roomType);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRoomType);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("FK_room_type_image")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "【圖片 ID】填寫不正確：資料庫中找不到此圖片 ID！"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "新增失敗，請檢查欄位格式與必填項目！"));
        }
    }

    // 4. 更新房型 (PUT /api/roomtypes/{id})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoomType(@PathVariable Integer id, @RequestBody RoomType roomType) {
        try {
            RoomType updatedRoomType = roomTypeService.update(id, roomType);
            return ResponseEntity.ok(updatedRoomType);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("FK_room_type_image")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "【圖片 ID】填寫不正確：資料庫中找不到此圖片 ID！"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "更新失敗：" + e.getMessage()));
        }
    }

    // 5. 刪除房型 (DELETE /api/roomtypes/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoomType(@PathVariable Integer id) {
        try {
            roomTypeService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "房型刪除成功！"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "刪除失敗：該房型可能已被其他資料關聯！"));
        }
    }
}