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

import com.hotel.model.entity.RoomTask;
import com.hotel.service.RoomTaskService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/roomtask")
public class RoomTaskController {

    private final RoomTaskService roomTaskService;

    public RoomTaskController(RoomTaskService roomTaskService) {
        this.roomTaskService = roomTaskService;
    }

    // 1. 新增房務任務 (POST /api/roomtask)
    @PostMapping
    public ResponseEntity<?> createRoomTask(@RequestBody RoomTask roomTask) {
        try {
            // 確保新增時主鍵為 null，由資料庫自動遞增
            roomTask.setTaskId(null);
            RoomTask savedTask = roomTaskService.insert(roomTask);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("FK_")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "外鍵約束錯誤：請確認關聯的房間 ID 或員工 ID 是否存在！"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "新增任務失敗：" + e.getMessage()));
        }
    }

    // 2. 查詢任務與條件過濾 (GET /api/roomtask)
    @GetMapping
    public ResponseEntity<?> getTasks(
            @RequestParam(required = false) Integer taskId,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String taskStatus,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) String priority) {

        try {
            // 帶入 taskId 時執行單筆精準查詢
            if (taskId != null) {
                RoomTask task = roomTaskService.findById(taskId);
                return ResponseEntity.ok(List.of(task));
            }
            if (roomId != null) {
                return ResponseEntity.ok(roomTaskService.findByRoomId(roomId));
            }
            if (taskStatus != null && !taskStatus.isBlank()) {
                return ResponseEntity.ok(roomTaskService.findByTaskStatus(taskStatus));
            }
            if (taskType != null && !taskType.isBlank()) {
                return ResponseEntity.ok(roomTaskService.findByTaskType(taskType));
            }
            if (employeeId != null) {
                return ResponseEntity.ok(roomTaskService.findByEmployeeId(employeeId));
            }
            if (priority != null && !priority.isBlank()) {
                return ResponseEntity.ok(roomTaskService.findByPriority(priority));
            }

            return ResponseEntity.ok(roomTaskService.findAll());

        } catch (EntityNotFoundException e) {
            // 查無特定 taskId 時回傳空陣列或 404
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "查詢任務失敗：" + e.getMessage()));
        }
    }

    // 3. 依 ID 查詢單筆任務 (GET /api/roomtask/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Integer id) {
        try {
            RoomTask task = roomTaskService.findById(id);
            return ResponseEntity.ok(task);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "系統錯誤：" + e.getMessage()));
        }
    }

    // 4. 更新任務 (PUT /api/roomtask/{id})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoomTask(@PathVariable Integer id, @RequestBody RoomTask taskDetails) {
        try {
            RoomTask updatedTask = roomTaskService.update(id, taskDetails);
            return ResponseEntity.ok(updatedTask);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "更新失敗：" + e.getMessage()));
        }
    }

    // 5. 刪除任務 (DELETE /api/roomtask/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoomTask(@PathVariable Integer id) {
        try {
            roomTaskService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "任務刪除成功！"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "刪除失敗：該任務可能已被其他資料關聯！"));
        }
    }
}