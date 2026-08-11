package com.hotel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.entity.RestaurantTime;
import com.hotel.repository.RestaurantTimeRepository;

@Service
public class RestaurantTimeService {
	
	private final RestaurantTimeRepository restaurantTimeRepository;

	public RestaurantTimeService(RestaurantTimeRepository restaurantTimeRepository) {
		this.restaurantTimeRepository = restaurantTimeRepository;
	}
	
	public List<RestaurantTime> findAllTimes(){
		return restaurantTimeRepository.findAll();
	}
	
	public RestaurantTime findById(Integer id) {
		return restaurantTimeRepository.findById(id).orElse(null);
	}
	
	public List<RestaurantTime> findByRestaurantId(Integer restaurantId){
		return restaurantTimeRepository.findByRestaurantId(restaurantId);
	}
	
	public RestaurantTime save(RestaurantTime restaurantTime) {
		return restaurantTimeRepository.save(restaurantTime);
	}
	
	public void deleteById(Integer id) {
		restaurantTimeRepository.deleteById(id);
	}
}
