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

import com.hotel.entity.RestaurantTime;
import com.hotel.service.RestaurantTimeService;

@RestController
@RequestMapping("/api/restaurant_times")
public class RestaurantTimeController {

	private final RestaurantTimeService restaurantTimeService;

	public RestaurantTimeController(RestaurantTimeService restaurantTimeService) {
		this.restaurantTimeService = restaurantTimeService;
	}

	@GetMapping
	public List<RestaurantTime> findAll() {
		return restaurantTimeService.findAllTimes();
	}

	@GetMapping("/{id}")
	public ResponseEntity<RestaurantTime> findById(@PathVariable Integer id) {
		RestaurantTime restaurantTime = restaurantTimeService.findById(id);

		if (restaurantTime == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(restaurantTime);
	}

	@GetMapping("/restaurant/{restaurantId}")
	public List<RestaurantTime> findByRestaurantId(@PathVariable Integer restaurantId) {
		return restaurantTimeService.findByRestaurantId(restaurantId);
	}

	@PostMapping
	public ResponseEntity<RestaurantTime> create(@RequestBody RestaurantTime restaurantTime) {
		restaurantTime.setTimeId(null);

		return ResponseEntity.status(HttpStatus.CREATED).body(restaurantTimeService.save(restaurantTime));
	}

	@PutMapping("/{id}")
	public ResponseEntity<RestaurantTime> update(@PathVariable Integer id,
			@RequestBody RestaurantTime formRestaurantTime) {
		RestaurantTime restaurantTime = restaurantTimeService.findById(id);

		if (restaurantTime == null) {
			return ResponseEntity.notFound().build();
		}

		restaurantTime.setRestaurantId(formRestaurantTime.getRestaurantId());
		restaurantTime.setMealType(formRestaurantTime.getMealType());
		restaurantTime.setOpenTime(formRestaurantTime.getOpenTime());
		restaurantTime.setCloseTime(formRestaurantTime.getCloseTime());

		return ResponseEntity.ok(restaurantTimeService.save(restaurantTime));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		RestaurantTime restaurantTime = restaurantTimeService.findById(id);

		if (restaurantTime == null) {
			return ResponseEntity.notFound().build();
		}

		restaurantTimeService.deleteById(id);

		return ResponseEntity.noContent().build();
	}
}
