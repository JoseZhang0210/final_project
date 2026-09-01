package com.hotel.service;

import java.util.List;
import java.util.Optional;

import com.hotel.model.dto.RoomTypeDTO;

public interface RoomTypeService {
    List<RoomTypeDTO> findAll();
    Optional<RoomTypeDTO> findOptionalById(Integer id);
    RoomTypeDTO insert(RoomTypeDTO roomTypeDTO);
    RoomTypeDTO update(Integer id, RoomTypeDTO updatedRoomTypeDTO);
    void deleteById(Integer id);
}