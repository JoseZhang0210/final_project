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

import com.hotel.model.dto.RoomDTO;
import com.hotel.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
        roomDTO.setRoomId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.insert(roomDTO));
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.findAll());
    }

    @GetMapping("/floor/{floor}")
    public ResponseEntity<List<RoomDTO>> getByFloor(@PathVariable Integer floor) {
        return ResponseEntity.ok(roomService.findByFloor(floor));
    }

    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<RoomDTO> getByRoomNumber(@PathVariable String roomNumber) {
        return roomService.findByRoomNumber(roomNumber)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("找不到房號為 " + roomNumber + " 的房間"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Integer id, @RequestBody RoomDTO roomDTO) {
        return ResponseEntity.ok(roomService.update(id, roomDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable Integer id) {
        roomService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "房間刪除成功！"));
    }
}