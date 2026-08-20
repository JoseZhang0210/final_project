package com.hotel.controller;

import java.util.List;

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

import com.hotel.entity.RoomTask;
import com.hotel.service.RoomTaskService;

@RestController
@RequestMapping("/api/roomtask")
public class RoomTaskController {

    private final RoomTaskService roomTaskService;

    public RoomTaskController(RoomTaskService roomTaskService) {
        this.roomTaskService = roomTaskService;
    }

    // Create
    @PostMapping
    public ResponseEntity<RoomTask> createRoomTask(@RequestBody RoomTask roomTask) {
        RoomTask savedTask = roomTaskService.insert(roomTask);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    // Read All
    @GetMapping
    public ResponseEntity<List<RoomTask>> getAllRoomTasks() {
        List<RoomTask> tasks = roomTaskService.findAll();
        return ResponseEntity.ok(tasks);
    }

    // Read by TaskID
    @GetMapping("/{id}")
    public ResponseEntity<RoomTask> getRoomTaskById(@PathVariable Integer id) {
        return roomTaskService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Read with Filter Options (roomId, status, type, priority)
    @GetMapping
    public ResponseEntity<List<RoomTask>> getTasks(
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String priority) {

        // 2. 僅查詢特定房號
        if (roomId != null) {
            return ResponseEntity.ok(roomTaskService.findByRoomId(roomId));
        }

        // 3. 僅查詢特定任務狀態 (例如：待處理)
        if (status != null) {
            return ResponseEntity.ok(roomTaskService.findByTaskStatus(status));
        }

        // 4. 僅查詢特定任務類型 (例如：退房清潔)
        if (type != null) {
            return ResponseEntity.ok(roomTaskService.findByTaskType(type));
        }

        // 5. 僅查詢特定優先度 (例如：緊急、普通、低)
        if (priority != null) {
            return ResponseEntity.ok(roomTaskService.findByPriority(priority));
        }

        // 6. 完全沒帶條件時，查詢全部任務
        return ResponseEntity.ok(roomTaskService.findAll());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<RoomTask> updateRoomTask(@PathVariable Integer id, @RequestBody RoomTask taskDetails) {
        try {
            RoomTask updatedTask = roomTaskService.update(id, taskDetails);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomTask(@PathVariable Integer id) {
        boolean deleted = roomTaskService.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}