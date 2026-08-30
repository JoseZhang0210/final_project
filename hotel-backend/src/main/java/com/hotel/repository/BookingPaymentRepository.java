package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.BookingPayment;

public interface BookingPaymentRepository extends JpaRepository<BookingPayment, Integer> {
}
