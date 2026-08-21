package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.model.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

        @Query("SELECT b FROM Booking b WHERE (:bookingId IS NULL OR b.bookingId = :bookingId) AND " +
                        "(:bookingOrderId IS NULL OR b.bookingOrderId = :bookingOrderId) AND " +
                        "(:bookingStatus IS NULL OR b.bookingStatus LIKE CONCAT('%', :bookingStatus, '%'))")
        List<Booking> search(
                        @Param("bookingId") Integer bookingId,
                        @Param("bookingOrderId") Integer bookingOrderId,
                        @Param("bookingStatus") String bookingStatus);

        void deleteByBookingOrderId(Integer id);

}
