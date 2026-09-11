package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Reservation;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

	List<Reservation> findByMemberId(Integer memberId);

	List<Reservation> findByRestaurantId(Integer restaurantId);

	@Query("""
				SELECT COALESCE(SUM(r.peopleCount), 0)
			       FROM Reservation r
			       WHERE r.restaurantId = :restaurantId
				AND r.timeId = :timeId
				AND r.reservationDate = :reservationDate
				AND r.status <> '已取消'
			""")
	Long sumPeopleByRestaurantAndTime(
			@Param("restaurantId") Integer restaurantId,
			@Param("timeId") Integer timeId,
			@Param("reservationDate") LocalDate reservationDate);
}
