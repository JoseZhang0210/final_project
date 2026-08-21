package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByMemberId(Integer memberId);

    List<Payment> findByPaymentStatus(String paymentStatus);

    List<Payment> findByPaymentMethod(String paymentMethod);
}
