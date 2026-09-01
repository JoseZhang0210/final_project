package com.hotel.service;

import java.util.List;
import java.util.Optional;

import com.hotel.model.dto.RoomTaskDTO;

public interface RoomTaskService {
    List<RoomTaskDTO> findAll();
    Optional<RoomTaskDTO> findOptionalById(Integer id);
    List<RoomTaskDTO> findByRoomId(Integer roomId);
    List<RoomTaskDTO> findByPriority(String priority);
    List<RoomTaskDTO> findByEmployeeId(Integer employeeId);
    RoomTaskDTO insert(RoomTaskDTO roomTaskDTO);
    RoomTaskDTO update(Integer id, RoomTaskDTO updatedTaskDTO);
    void deleteById(Integer id);
}