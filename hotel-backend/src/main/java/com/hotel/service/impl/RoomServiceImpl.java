package com.hotel.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.RoomDTO;
import com.hotel.model.entity.Room;
import com.hotel.repository.RoomRepository;
import com.hotel.service.RoomService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final com.hotel.repository.BookingRepository bookingRepository;

    public RoomServiceImpl(RoomRepository roomRepository, com.hotel.repository.BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> findAll() {
        return roomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> findByFloor(Integer floor) {
        return roomRepository.findByFloor(floor).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomDTO> findByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber).map(this::convertToDTO);
    }

    @Override
    public RoomDTO insert(RoomDTO roomDTO) {
        Room room = convertToEntity(roomDTO);
        Room saved = roomRepository.save(room);
        return convertToDTO(saved);
    }

    @Override
    public RoomDTO update(Integer id, RoomDTO updatedRoomDTO) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的房間資料"));

        
        if (updatedRoomDTO.getRoomStatus() != null) {
            if (("維修中".equals(updatedRoomDTO.getRoomStatus()) || "停用".equals(updatedRoomDTO.getRoomStatus())) && !updatedRoomDTO.getRoomStatus().equals(existingRoom.getRoomStatus())) {
                boolean hasFutureBookings = bookingRepository.existsByRoomIdAndCheckOutDateGreaterThanEqual(id, java.time.LocalDate.now());
                if (hasFutureBookings) {
                    throw new IllegalStateException("該房間有未來的預訂，請為顧客更換房間或免費升等");
                }
            }
            existingRoom.setRoomStatus(updatedRoomDTO.getRoomStatus());
        }

        if (updatedRoomDTO.getRoomTypeId() != null) {
            existingRoom.setRoomTypeId(updatedRoomDTO.getRoomTypeId());
        }
        
        return convertToDTO(existingRoom);
    }

    @Override
    public void deleteById(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的房間 ID: " + id + " 不存在");
        }
        roomRepository.deleteById(id);
    }

    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setRoomId(room.getRoomId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomTypeId(room.getRoomTypeId());
        dto.setFloor(room.getFloor());
        dto.setRoomStatus(room.getRoomStatus());
        return dto;
    }

    private Room convertToEntity(RoomDTO dto) {
        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomTypeId(dto.getRoomTypeId());
        room.setFloor(dto.getFloor());
        room.setRoomStatus(dto.getRoomStatus());
        return room;
    }
}
