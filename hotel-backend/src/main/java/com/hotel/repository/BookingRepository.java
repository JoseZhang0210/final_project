package com.hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Booking;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {

        // 2. 依入住日期查詢
        List<Booking> findByCheckInDate(LocalDate checkInDate);

        boolean existsByRoomIdAndCheckOutDateGreaterThanEqual(Integer roomId, LocalDate date);

        // 3. 依訂房狀態查詢
        List<Booking> findByBookingStatus(String bookingStatus);

        // 4. 尋找特定日期區間內，特定房型已經被預訂的房間 ID (排除已取消的訂單)
        @Query("SELECT b.roomId FROM Booking b WHERE b.roomTypeId = :roomTypeId AND b.bookingStatus != '已取消' AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate AND b.roomId IS NOT NULL")
        List<Integer> findBookedRoomIds(
            @Param("roomTypeId") Integer roomTypeId, 
            @Param("checkInDate") LocalDate checkInDate, 
            @Param("checkOutDate") LocalDate checkOutDate
        );
}
