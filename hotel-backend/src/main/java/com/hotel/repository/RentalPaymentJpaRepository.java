package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.RentalPayment;

public interface RentalPaymentJpaRepository extends JpaRepository<RentalPayment, Integer> {
}

