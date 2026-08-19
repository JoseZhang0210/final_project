package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

}
