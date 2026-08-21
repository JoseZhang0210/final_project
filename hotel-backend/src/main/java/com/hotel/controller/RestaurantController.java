package com.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Restaurant;
import com.hotel.service.RestaurantService;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

	private final RestaurantService restaurantService;

	public RestaurantController(RestaurantService restaurantService) {
		this.restaurantService = restaurantService;
	}

	@GetMapping
	public List<Restaurant> findAll() {
		return restaurantService.findAllRestaurants();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Restaurant> findById(@PathVariable Integer id) {
		Restaurant restaurant = restaurantService.findById(id);

		if (restaurant == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(restaurant);
	}

	@PostMapping
	public ResponseEntity<Restaurant> create(@RequestBody Restaurant restaurant) {

		restaurant.setRestaurantId(null);

		Restaurant saveRestaurant = restaurantService.save(restaurant);

		return ResponseEntity.status(HttpStatus.CREATED).body(saveRestaurant);

	}

	@PutMapping("/{id}")
	public ResponseEntity<Restaurant> update(@PathVariable Integer id, @RequestBody Restaurant formRestaurant) {

		Restaurant restaurant = restaurantService.findById(id);

		if (restaurant == null) {
			return ResponseEntity.notFound().build();
		}

		restaurant.setRestaurantName(formRestaurant.getRestaurantName());
		restaurant.setAddress(formRestaurant.getAddress());
		restaurant.setPhone(formRestaurant.getPhone());
		restaurant.setCapacity(formRestaurant.getCapacity());
		restaurant.setDescription(formRestaurant.getDescription());

		return ResponseEntity.ok(restaurantService.save(restaurant));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		Restaurant restaurant = restaurantService.findById(id);

		if (restaurant == null) {
			return ResponseEntity.notFound().build();
		}

		restaurantService.deleteById(id);

		return ResponseEntity.noContent().build();
	}
}
