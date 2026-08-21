package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.Room;
import com.hotel.repository.RoomRepository;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Create
    public Room insert(Room room) {
        return roomRepository.save(room);
    }

    // Read All
    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Room> findByFloor(Integer floor) {
        return roomRepository.findByFloor(floor);
    }

    // Read by RoomTypeId
    @Transactional(readOnly = true)
    public List<Room> findByRoomTypeId(Integer roomTypeId) {
        return roomRepository.findByRoomTypeId(roomTypeId);
    }

    // Read by ID
    @Transactional(readOnly = true)
    public Optional<Room> findById(Integer id) {
        return roomRepository.findById(id);
    }

    // Update
    public Room update(Integer id, Room updatedRoom) {
        return roomRepository.findById(id)
                .map(room -> {
                    if (updatedRoom.getRoomNumber() != null) {
                        room.setRoomNumber(updatedRoom.getRoomNumber());
                    }
                    if (updatedRoom.getRoomTypeId() != null) {
                        room.setRoomTypeId(updatedRoom.getRoomTypeId());
                    }
                    if (updatedRoom.getFloor() != null) {
                        room.setFloor(updatedRoom.getFloor());
                    }
                    if (updatedRoom.getRoomStatus() != null) {
                        room.setRoomStatus(updatedRoom.getRoomStatus());
                    }
                    return roomRepository.save(room);
                })
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    // Delete By Id
    public boolean deleteById(Integer id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
