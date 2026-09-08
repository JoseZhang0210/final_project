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
import com.hotel.service.SmsService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final SmsService smsService;

    public ReservationController(
            ReservationService reservationService,
            SmsService smsService) {

        this.reservationService = reservationService;
        this.smsService = smsService;
    }

    @GetMapping
    public List<Reservation> findAll() {
        return reservationService.findAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> findById(@PathVariable Integer id) {
        Reservation reservation = reservationService.findById(id);

        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/member/{memberId}")
    public List<Reservation> findByMemberId(@PathVariable Integer memberId) {
        return reservationService.findByMemberId(memberId);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<Reservation> findByRestaurantId(
            @PathVariable Integer restaurantId) {
        return reservationService.findByRestaurantId(restaurantId);
    }

    @PostMapping
    public ResponseEntity<Reservation> create(
            @RequestBody Reservation reservation) {

        if (isGuestWithoutContact(reservation)) {
            return ResponseEntity.badRequest().build();
        }

        reservation.setReservationId(null);
        setDefaultStatus(reservation);

        Reservation savedReservation = reservationService.save(reservation);
        sendReservationSms(savedReservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> update(
            @PathVariable Integer id,
            @RequestBody Reservation formReservation) {

        Reservation reservation = reservationService.findById(id);

        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        if (isGuestWithoutContact(formReservation)) {
            return ResponseEntity.badRequest().build();
        }

        reservation.setMemberId(formReservation.getMemberId());
        reservation.setContactName(formReservation.getContactName());
        reservation.setContactPhone(formReservation.getContactPhone());
        reservation.setRestaurantId(formReservation.getRestaurantId());
        reservation.setReservationDate(formReservation.getReservationDate());
        reservation.setTimeId(formReservation.getTimeId());
        reservation.setPeopleCount(formReservation.getPeopleCount());
        reservation.setStatus(formReservation.getStatus());
        setDefaultStatus(reservation);

        return ResponseEntity.ok(reservationService.save(reservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (reservationService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isGuestWithoutContact(Reservation reservation) {
        return reservation.getMemberId() == null
                && (reservation.getContactName() == null
                        || reservation.getContactName().isBlank()
                        || reservation.getContactPhone() == null
                        || reservation.getContactPhone().isBlank());
    }

    private void setDefaultStatus(Reservation reservation) {
        if (reservation.getStatus() == null || reservation.getStatus().isBlank()) {
            reservation.setStatus("已訂位");
        }
    }

    // 會員訂位成功後通知；簡訊失敗不影響訂位成立。
    private void sendReservationSms(Reservation reservation) {
        if (reservation.getContactPhone() == null
                || reservation.getContactPhone().isBlank()) {
            return;
        }

        String message = "【星澄飯店】您的訂位已完成，訂位編號 #"
                + reservation.getReservationId();

        try {
            smsService.send(reservation.getContactPhone(), message);
        } catch (Exception e) {
            System.out.println("訂位簡訊發送失敗：" + e.getMessage());
        }
    }
}
