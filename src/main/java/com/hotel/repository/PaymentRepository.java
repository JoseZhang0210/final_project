package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
