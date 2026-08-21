package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

}
