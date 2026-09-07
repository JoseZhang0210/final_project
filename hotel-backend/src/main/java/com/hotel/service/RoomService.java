package com.hotel.service;

import java.util.List;
import java.util.Optional;

import com.hotel.model.dto.RoomDTO;

public interface RoomService {
    List<RoomDTO> findAll();
    List<RoomDTO> findByFloor(Integer floor);
    Optional<RoomDTO> findByRoomNumber(String roomNumber);
    RoomDTO insert(RoomDTO roomDTO);
    RoomDTO update(Integer id, RoomDTO updatedRoomDTO);
    void deleteById(Integer id);
}