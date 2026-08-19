package com.hotel.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Query("""
    SELECT r FROM Room r 
    WHERE r.roomTypeId = :roomTypeId 
    AND r.roomId NOT IN (
        SELECT b.room.roomId FROM Booking b 
        WHERE b.checkInDate < :checkOutDate 
          AND b.checkOutDate > :checkInDate 
          AND (
              b.bookingOrder.orderStatus = 'PAID' 
              OR (b.bookingOrder.orderStatus = 'PENDING' AND b.bookingOrder.createdAt >= :fifteenMinsAgo)
          )
    )
""")
List<Room> findAvailableRooms(
    @Param("roomTypeId") Integer roomTypeId,
    @Param("checkInDate") LocalDate checkInDate,
    @Param("checkOutDate") LocalDate checkOutDate,
    @Param("fifteenMinsAgo") LocalDateTime fifteenMinsAgo
);
}