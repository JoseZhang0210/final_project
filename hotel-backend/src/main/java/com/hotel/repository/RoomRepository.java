package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.model.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByFloor(Integer floor);

    // List<Room> findByRoomTypeId(Integer roomTypeId);

    List<Room> findByRoomType_RoomTypeId(Integer roomTypeId);

    @Query("SELECT r FROM Room r WHERE r.roomType.roomTypeId = :roomTypeId")
    List<Room> findByRoomTypeId(@Param("roomTypeId") Integer roomTypeId);
}