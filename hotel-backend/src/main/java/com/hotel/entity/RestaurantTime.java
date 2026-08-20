package com.hotel.entity;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer timeId;
	private Integer restaurantId;
	private String mealType;
	private LocalTime openTime;
	private LocalTime closeTime;

}
