package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.BookingPayment;

import java.util.Optional;

public interface BookingPaymentRepository extends JpaRepository<BookingPayment, Integer> {
    Optional<BookingPayment> findByBookingId(Integer bookingId);
}
