package com.hotel.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Reservation;
import com.hotel.model.entity.Restaurant;
import com.hotel.model.entity.RestaurantTime;
import com.hotel.service.ReservationService;
import com.hotel.service.RestaurantService;
import com.hotel.service.RestaurantTimeService;
import com.hotel.service.SmsService;

@RestController
@RequestMapping("/api/public")
public class PublicReservationController {

    private final RestaurantService restaurantService;
    private final RestaurantTimeService restaurantTimeService;
    private final ReservationService reservationService;
    private final SmsService smsService;

    public PublicReservationController(
            RestaurantService restaurantService,
            RestaurantTimeService restaurantTimeService,
            ReservationService reservationService,
            SmsService smsService) {

        this.restaurantService = restaurantService;
        this.restaurantTimeService = restaurantTimeService;
        this.reservationService = reservationService;
        this.smsService = smsService;
    }

    // 給前台讀取餐廳
    @GetMapping("/restaurants")
    public List<Restaurant> findAllRestaurants() {
        return restaurantService.findAllRestaurants();
    }

    // 給前台依餐廳讀取時段
    @GetMapping("/restaurants/{restaurantId}/times")
    public List<RestaurantTime> findTimesByRestaurantId(
            @PathVariable Integer restaurantId) {

        return restaurantTimeService.findByRestaurantId(restaurantId);
    }

    // 訪客新增訂位
    @PostMapping("/reservations")
    public ResponseEntity<?> createGuestReservation(
            @RequestBody Reservation reservation) {

        if (reservation.getContactName() == null
                || reservation.getContactName().isBlank()
                || reservation.getContactPhone() == null
                || reservation.getContactPhone().isBlank()
                || reservation.getRestaurantId() == null
                || reservation.getTimeId() == null
                || reservation.getReservationDate() == null
                || reservation.getPeopleCount() == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "請完整填寫訂位資料。"));
        }

        if (reservation.getReservationDate().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "不可選擇今天以前的日期。"));
        }

        if (reservation.getPeopleCount() < 1) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "訂位人數至少為 1 人。"));
        }

        if (!reservation.getContactPhone().matches("^09\\d{8}$")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "電話請填寫 09 開頭的 10 碼手機號碼。"));
        }

        Restaurant restaurant = restaurantService.findById(reservation.getRestaurantId());
        RestaurantTime restaurantTime = restaurantTimeService.findById(reservation.getTimeId());

        if (restaurant == null || restaurantTime == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "餐廳或時段資料不存在。"));
        }

        if (!restaurantTime.getRestaurantId().equals(reservation.getRestaurantId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "選擇的時段不屬於此餐廳。"));
        }

        reservation.setReservationId(null);
        reservation.setMemberId(null);
        reservation.setStatus("已訂位");

        Reservation savedReservation = reservationService.save(reservation);
        sendReservationSms(savedReservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    // 訂位成功後通知訪客；簡訊失敗不影響訂位成立。
    private void sendReservationSms(Reservation reservation) {
        String message = "【星澄飯店】您的訂位已完成，訂位編號 #"
                + reservation.getReservationId();

        try {
            smsService.send(reservation.getContactPhone(), message);
        } catch (Exception e) {
            System.out.println("訂位簡訊發送失敗：" + e.getMessage());
        }
    }
}
