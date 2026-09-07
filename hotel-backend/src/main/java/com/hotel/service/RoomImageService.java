package com.hotel.service;

import java.util.List;
import java.util.Optional;

import com.hotel.model.dto.RoomImageDTO;

public interface RoomImageService {
    List<RoomImageDTO> findAll();
    Optional<RoomImageDTO> findOptionalById(Integer id);
    RoomImageDTO insert(RoomImageDTO imageDTO);
    RoomImageDTO update(Integer id, RoomImageDTO updatedImageDTO);
    void deleteById(Integer id);
}