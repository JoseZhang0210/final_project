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

import com.hotel.model.entity.Reservation;
import com.hotel.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService) {

        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Reservation> findAll() {
        return reservationService.findAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> findById(
            @PathVariable Integer id) {

        Reservation reservation = reservationService.findById(id);

        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/member/{memberId}")
    public List<Reservation> findByMemberId(
            @PathVariable Integer memberId) {

        return reservationService.findByMemberId(memberId);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<Reservation> findByRestaurantId(
            @PathVariable Integer restaurantId) {

        return reservationService
                .findByRestaurantId(restaurantId);
    }

    @PostMapping
    public ResponseEntity<Reservation> create(
            @RequestBody Reservation reservation) {

        if (reservation.getMemberId() == null
                && !hasContactInfo(reservation)) {

            return ResponseEntity.badRequest().build();
        }

        reservation.setReservationId(null);

        if (reservation.getStatus() == null
                || reservation.getStatus().isBlank()) {

            reservation.setStatus("已訂位");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationService.save(reservation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> update(
            @PathVariable Integer id,
            @RequestBody Reservation formReservation) {

        Reservation reservation = reservationService.findById(id);

        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        if (formReservation.getMemberId() == null
                && !hasContactInfo(formReservation)) {

            return ResponseEntity.badRequest().build();
        }

        reservation.setMemberId(formReservation.getMemberId());
        reservation.setContactName(formReservation.getContactName());
        reservation.setContactPhone(formReservation.getContactPhone());
        reservation.setRestaurantId(formReservation.getRestaurantId());
        reservation.setReservationDate(
                formReservation.getReservationDate());
        reservation.setTimeId(formReservation.getTimeId());
        reservation.setPeopleCount(formReservation.getPeopleCount());
        reservation.setStatus(formReservation.getStatus());

        return ResponseEntity.ok(
                reservationService.save(reservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Reservation reservation = reservationService.findById(id);

        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        reservationService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private boolean hasContactInfo(Reservation reservation) {
        return reservation.getContactName() != null
                && !reservation.getContactName().isBlank()
                && reservation.getContactPhone() != null
                && !reservation.getContactPhone().isBlank();
    }
}