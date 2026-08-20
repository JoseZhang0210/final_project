package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	
	List<Reservation> findByMemberId(Integer memberId);

	List<Reservation> findByRestaurantId(Integer restaurantId);
}
