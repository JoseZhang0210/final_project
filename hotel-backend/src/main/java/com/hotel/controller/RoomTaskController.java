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

import com.hotel.model.dto.RoomTaskDTO;
import com.hotel.service.RoomTaskService;

@RestController
@RequestMapping("/api/roomtask")
public class RoomTaskController {

    private final RoomTaskService roomTaskService;

    public RoomTaskController(RoomTaskService roomTaskService) {
        this.roomTaskService = roomTaskService;
    }

    @PostMapping
    public ResponseEntity<RoomTaskDTO> createRoomTask(@RequestBody RoomTaskDTO roomTaskDTO) {
        roomTaskDTO.setTaskId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomTaskService.insert(roomTaskDTO));
    }

    @GetMapping
    public ResponseEntity<List<RoomTaskDTO>> getTasks(
            @RequestParam(required = false) Integer taskId,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) String priority) {

        if (taskId != null) {
            return roomTaskService.findOptionalById(taskId)
                    .map(task -> ResponseEntity.ok(List.of(task)))
                    .orElseGet(() -> ResponseEntity.ok(List.of()));
        }
        if (roomId != null) {
            return ResponseEntity.ok(roomTaskService.findByRoomId(roomId));
        }
        if (employeeId != null) {
            return ResponseEntity.ok(roomTaskService.findByEmployeeId(employeeId));
        }
        if (priority != null && !priority.isBlank()) {
            return ResponseEntity.ok(roomTaskService.findByPriority(priority));
        }

        return ResponseEntity.ok(roomTaskService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomTaskDTO> getTaskById(@PathVariable Integer id) {
        return roomTaskService.findOptionalById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("找不到 ID 為 " + id + " 的任務資料"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomTaskDTO> updateRoomTask(@PathVariable Integer id, @RequestBody RoomTaskDTO taskDTO) {
        return ResponseEntity.ok(roomTaskService.update(id, taskDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRoomTask(@PathVariable Integer id) {
        roomTaskService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "任務刪除成功！"));
    }
}