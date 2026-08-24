package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.model.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    // List<Payment> findByPaymentStatus(String paymentStatus);

    List<Payment> findByPaymentMethod(String paymentMethod);

    // 透過 BookingOrder 跨表查詢 Member 的 Payment
    @Query("SELECT p FROM Payment p WHERE p.bookingOrder.member.memberId = :memberId")
    List<Payment> findByMemberId(@Param("memberId") Integer memberId);

}