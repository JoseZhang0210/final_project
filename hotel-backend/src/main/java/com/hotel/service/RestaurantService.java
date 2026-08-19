package com.hotel.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.hotel.entity.Restaurant;
import com.hotel.repository.RestaurantRepository;

@Service
public class RestaurantService {
	
	private final RestaurantRepository restaurantRepository;

	public RestaurantService(RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
	}
	
	public List<Restaurant> findAllRestaurants() {
	    return restaurantRepository.findAll();
	}
	
	public Restaurant findById(Integer id) {
	    return restaurantRepository.findById(id).orElse(null);
	}

	public Restaurant save(Restaurant restaurant) {
	    return restaurantRepository.save(restaurant);
	}

	public void deleteById(Integer id) {
	    restaurantRepository.deleteById(id);
	}

}