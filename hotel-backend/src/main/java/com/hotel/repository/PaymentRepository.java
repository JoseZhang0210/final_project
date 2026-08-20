package com.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    Optional<Payment> findByMemberId(Integer memberId);
}

