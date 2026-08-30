package com.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByFloor(Integer floor);

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByRoomTypeId(Integer roomTypeId);

}