package com.hotel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.entity.Reservation;
import com.hotel.repository.ReservationRepository;

@Service
public class ReservationService {
	
	private final ReservationRepository reservationRepository;

	public ReservationService(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}
	
	public List<Reservation> findAllReservations(){
		return reservationRepository.findAll();
	}
	
	public Reservation findById(Integer id) {
		return reservationRepository.findById(id).orElse(null);
	}
	
	public List<Reservation> findByMemberId(Integer memberId){
		return reservationRepository.findByMemberId(memberId);
	}
	
	public List<Reservation> findByRestaurantId(Integer restaurantId){
		return reservationRepository.findByRestaurantId(restaurantId);
	}
	
	public Reservation save(Reservation reservation) {
		return reservationRepository.save(reservation);
	}
	
	public void deleteById(Integer id) {
		reservationRepository.deleteById(id);
	}
}
