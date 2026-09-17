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

import com.hotel.model.dto.EmailDTO;
import com.hotel.model.entity.Reservation;
import com.hotel.model.entity.Restaurant;
import com.hotel.model.entity.RestaurantTime;
import com.hotel.service.ReservationService;
import com.hotel.service.RestaurantService;
import com.hotel.service.RestaurantTimeService;
import com.hotel.util.MailUtil;

@RestController
@RequestMapping("/api/public")
public class PublicReservationController {

    private final RestaurantService restaurantService;
    private final RestaurantTimeService restaurantTimeService;
    private final ReservationService reservationService;
    private final MailUtil mailUtil;

    public PublicReservationController(
            RestaurantService restaurantService,
            RestaurantTimeService restaurantTimeService,
            ReservationService reservationService,
            MailUtil mailUtil) {

        this.restaurantService = restaurantService;
        this.restaurantTimeService = restaurantTimeService;
        this.reservationService = reservationService;
        this.mailUtil = mailUtil;
    }

    @GetMapping("/restaurants")
    public List<Restaurant> findAllRestaurants() {
        return restaurantService.findAllRestaurants();
    }

    @GetMapping("/restaurants/{restaurantId}/times")
    public List<RestaurantTime> findTimesByRestaurantId(
            @PathVariable Integer restaurantId) {

        return restaurantTimeService.findByRestaurantId(restaurantId);
    }

    @PostMapping("/reservations")
    public ResponseEntity<?> createGuestReservation(
            @RequestBody Reservation reservation) {

        if (isReservationIncomplete(reservation)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "請完整填寫訂位資料與聯絡信箱。"));
        }

        if (!isValidEmail(reservation.getContactEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "請填寫正確的 Email 格式。"));
        }

        if (reservation.getReservationDate().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "不可選擇今天以前的日期。"));
        }

        if (reservation.getPeopleCount() < 1) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "訂位人數至少為 1 人。"));
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

        if (restaurant.getCapacity() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "此餐廳尚未設定可訂位人數。"));
        }

        Long bookedPeople = reservationService.sumPeopleByRestaurantAndTime(
                reservation.getRestaurantId(),
                reservation.getTimeId(),
                reservation.getReservationDate());

        int remainingSeats = restaurant.getCapacity() - bookedPeople.intValue();

        if (reservation.getPeopleCount() > remainingSeats) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "此時段僅剩 " + Math.max(remainingSeats, 0)
                                    + " 個座位，無法完成訂位。"));
        }

        reservation.setReservationId(null);
        reservation.setMemberId(null);
        reservation.setStatus("已訂位");

        Reservation savedReservation = reservationService.save(reservation);
        sendReservationEmail(savedReservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    private boolean isReservationIncomplete(Reservation reservation) {
        return reservation.getContactName() == null
                || reservation.getContactName().isBlank()
                || reservation.getContactEmail() == null
                || reservation.getContactEmail().isBlank()
                || reservation.getRestaurantId() == null
                || reservation.getTimeId() == null
                || reservation.getReservationDate() == null
                || reservation.getPeopleCount() == null;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private void sendReservationEmail(Reservation reservation) {
        String subject = "【星澄飯店】訂位確認 #" + reservation.getReservationId();
        String content = """
                <h2>訂位已完成</h2>
                <p>親愛的 %s，您好：</p>
                <p>感謝您的訂位，以下是您的訂位資訊。</p>
                <ul>
                    <li>訂位編號：#%s</li>
                    <li>訂位日期：%s</li>
                    <li>訂位人數：%s 人</li>
                </ul>
                <p>星澄飯店期待您的蒞臨。</p>
                """.formatted(
                escapeHtml(reservation.getContactName()),
                reservation.getReservationId(),
                reservation.getReservationDate(),
                reservation.getPeopleCount());

        try {
            mailUtil.sendEmail(new EmailDTO(
                    reservation.getContactEmail(), subject, content, true));

            System.out.println("訂位確認信已加入寄送佇列："
                    + reservation.getContactEmail());
        } catch (Exception e) {
            // 寄信失敗不可影響訂位成立。
            System.err.println("訂位確認信發送失敗：" + e.getMessage());
        }
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
