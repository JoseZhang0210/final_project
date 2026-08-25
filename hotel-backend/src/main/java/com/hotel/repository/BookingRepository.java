package com.hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

        // 2. 依入住日期查詢
        List<Booking> findByCheckInDate(LocalDate checkInDate);

        // 3. 依訂房狀態查詢
        List<Booking> findByBookingStatus(String bookingStatus);
}
