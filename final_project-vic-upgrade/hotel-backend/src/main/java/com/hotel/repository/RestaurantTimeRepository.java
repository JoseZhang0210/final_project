package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.RestaurantTime;

public interface RestaurantTimeRepository extends JpaRepository<RestaurantTime, Integer> {

	List<RestaurantTime> findByRestaurantId(Integer restaurantId);
}
