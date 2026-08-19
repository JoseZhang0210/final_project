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
import org.springframework.web.bind.annotation.RestController;

import com.hotel.entity.RoomTask;
import com.hotel.service.RoomTaskService;

@RestController
@RequestMapping("/api/roomtasks")
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

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomTask> getRoomTaskById(@PathVariable Integer id) {
        return roomTaskService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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