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
        Restaurant restaurant = restaurantService.findById(
                reservation.getRestaurantId());

        RestaurantTime restaurantTime = restaurantTimeService.findById(
                reservation.getTimeId());

        String restaurantName = restaurant != null
                ? restaurant.getRestaurantName()
                : "未提供";

        String diningTime = restaurantTime != null
                ? restaurantTime.getMealType() + "（"
                        + restaurantTime.getOpenTime() + " - "
                        + restaurantTime.getCloseTime() + "）"
                : "未提供";
        String subject = "【星澄飯店】訂位確認 #" + reservation.getReservationId();
        String content = """
                                <!DOCTYPE html>
                                <html>
                                <body style="margin:0;padding:0;background:#f5f1eb;
                                             font-family:'Microsoft JhengHei',Arial,sans-serif;color:#3d3328;">
                                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0"
                                           style="padding:32px 12px;background:#f5f1eb;">
                                        <tr>
                                            <td align="center">
                                                <table role="presentation" width="600" cellspacing="0" cellpadding="0"
                                                       style="max-width:600px;background:#ffffff;border-radius:12px;
                                                              overflow:hidden;border:1px solid #e6dccf;">

                                                    <tr>
                                                        <td style="padding:24px 32px;background:#4d3c2b;color:#ffffff;">
                                                            <div style="font-size:13px;letter-spacing:1px;color:#e7c58c;">
                                                                XINGCHENG HOTEL
                                                            </div>
                                                            <div style="margin-top:6px;font-size:25px;font-weight:bold;">
                                                                餐廳訂位確認
                                                            </div>
                                                        </td>
                                                    </tr>

                                                    <tr>
                                                        <td style="padding:30px 32px;">
                                                            <h2 style="margin:0 0 16px;font-size:25px;color:#4d3c2b;">
                                                                您的訂位已完成
                                                            </h2>

                                                            <p style="margin:0 0 22px;line-height:1.9;font-size:16px;color:#4d3c2b;">
                                                                親愛的 %s，您好：<br>
                                                                感謝您的訂位，以下是本次用餐資訊。
                                                            </p>

                                                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0"
                                                                   style="border:1px solid #eadfce;border-radius:8px;background:#fcfaf7;">
                                                                <tr>
                                                                    <td style="padding:12px 16px;border-bottom:1px solid #eadfce;color:#80684b;font-size:16px">
                                                                        訂位編號
                                                                    </td>
                                                                    <td style="padding:12px 16px;border-bottom:1px solid #eadfce;font-weight:bold;">
                                                                        #%s
                                                                    </td>
                                                                </tr>
                                                                <tr>
                    <td style="padding:12px 16px;border-bottom:1px solid #eadfce;
                               color:#80684b;font-size:16px;">
                        用餐餐廳
                    </td>
                    <td style="padding:12px 16px;border-bottom:1px solid #eadfce;font-size:16px;">
                        %s
                    </td>
                </tr>
                <tr>
                    <td style="padding:12px 16px;border-bottom:1px solid #eadfce;
                               color:#80684b;font-size:16px;">
                        用餐時段
                    </td>
                    <td style="padding:12px 16px;border-bottom:1px solid #eadfce;font-size:16px;">
                        %s
                    </td>
                </tr>
                                                                <tr>
                                                                    <td style="padding:12px 16px;border-bottom:1px solid #eadfce;color:#80684b;font-size:16px;">
                                                                        訂位日期
                                                                    </td>
                                                                    <td style="padding:12px 16px;border-bottom:1px solid #eadfce;">
                                                                        %s
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td style="padding:12px 16px;color:#80684b;font-size:16px;">
                                                                        用餐人數
                                                                    </td>
                                                                    <td style="padding:12px 16px;font-size:16px;">
                                                                        %s 人
                                                                    </td>
                                                                </tr>
                                                            </table>

                                                            <p style="margin:22px 0 0;line-height:1.8;font-size:16px;">
                                                                星澄飯店期待您的蒞臨，祝您有愉快的用餐時光。
                                                            </p>
                                                        </td>
                                                    </tr>

                                                    <tr>
                                                        <td style="padding:16px 32px;background:#f1ebe3;
                                                                   color:#80684b;font-size:12px;text-align:center;font-size:16px;">
                                                            此為系統自動寄送通知，請勿直接回覆本信件。
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
                                    </table>
                                </body>
                                </html>
                                """
                .formatted(
                        escapeHtml(reservation.getContactName()),
                        reservation.getReservationId(),
                        restaurantName,
                        diningTime,
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
