package com.hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

        // @Query("SELECT b FROM Booking b WHERE (:bookingId IS NULL OR b.bookingId =
        // :bookingId) AND " +
        // "(:bookingOrderId IS NULL OR b.bookingOrder.bookingOrderId = :bookingOrderId)
        // AND " +
        // "(:bookingStatus IS NULL OR b.bookingStatus LIKE CONCAT('%', :bookingStatus,
        // '%'))")
        // List<Booking> search(
        // @Param("bookingId") Integer bookingId,
        // @Param("bookingOrderId") Integer bookingOrderId,
        // @Param("bookingStatus") String bookingStatus);

        // 2. 依入住日期查詢
        List<Booking> findByCheckInDate(LocalDate checkInDate);

        // 3. 依訂房狀態查詢
        List<Booking> findByBookingStatus(String bookingStatus);
}
