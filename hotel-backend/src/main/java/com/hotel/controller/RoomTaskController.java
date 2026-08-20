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

    // Read with Filter Options (roomId, status, type, priority)
    @GetMapping
    public ResponseEntity<List<RoomTask>> getTasks(
            @RequestParam(required = false) Integer taskId,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String taskStatus,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) String priority) {

        // 單一條件依序過濾
        if (taskId != null) {
            return roomTaskService.findById(taskId)
                    .map(List::of)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        if (roomId != null)
            return ResponseEntity.ok(roomTaskService.findByRoomId(roomId));
        if (taskStatus != null)
            return ResponseEntity.ok(roomTaskService.findByTaskStatus(taskStatus));
        if (taskType != null)
            return ResponseEntity.ok(roomTaskService.findByTaskType(taskType));
        if (employeeId != null)
            return ResponseEntity.ok(roomTaskService.findByEmployeeId(employeeId));
        if (priority != null)
            return ResponseEntity.ok(roomTaskService.findByPriority(priority));

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