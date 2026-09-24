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

import com.hotel.model.dto.EmailDTO;
import com.hotel.model.entity.Reservation;
import com.hotel.model.entity.Restaurant;
import com.hotel.model.entity.RestaurantTime;
import com.hotel.service.ReservationService;
import com.hotel.service.RestaurantService;
import com.hotel.service.RestaurantTimeService;
import com.hotel.util.MailUtil;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final RestaurantService restaurantService;
    private final RestaurantTimeService restaurantTimeService;
    private final MailUtil mailUtil;

    public ReservationController(
            ReservationService reservationService,
            RestaurantService restaurantService,
            RestaurantTimeService restaurantTimeService,
            MailUtil mailUtil) {

        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
        this.restaurantTimeService = restaurantTimeService;
        this.mailUtil = mailUtil;
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
    public ResponseEntity<?> create(@RequestBody Reservation reservation) {
        if (isContactIncomplete(reservation)) {
            return ResponseEntity.badRequest()
                    .body("請填寫訂位人姓名與聯絡信箱。");
        }

        if (!isValidEmail(reservation.getContactEmail())) {
            return ResponseEntity.badRequest()
                    .body("請填寫正確的 Email 格式。");
        }

        reservation.setReservationId(null);
        setDefaultStatus(reservation);

        Reservation savedReservation = reservationService.save(reservation);
        sendReservationEmail(savedReservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody Reservation formReservation) {

        Reservation reservation = reservationService.findById(id);

        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        if (formReservation.getContactName() == null
                || formReservation.getContactName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("請填寫訂位人姓名。");
        }

        reservation.setMemberId(formReservation.getMemberId());
        reservation.setContactName(formReservation.getContactName());
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

    private boolean isContactIncomplete(Reservation reservation) {
        return reservation.getContactName() == null
                || reservation.getContactName().isBlank()
                || reservation.getContactEmail() == null
                || reservation.getContactEmail().isBlank();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private void setDefaultStatus(Reservation reservation) {
        if (reservation.getStatus() == null || reservation.getStatus().isBlank()) {
            reservation.setStatus("已訂位");
        }
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
                                                                                    <td style="padding:24px 32px;background:#3f3326;color:#ffffff;text-align:center;">
                                                                                        <div style="font-size:25px;font-weight:bold;letter-spacing:3px;color:#e7c44d;">
                                                                                            星澄飯店
                                                                                        </div>
                                                                                        <div style="margin-top:8px;font-size:14px;letter-spacing:0.5px;color:#f5f1eb;">
                                                                                            Grand Aster Hotel &amp; Resorts
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
                    <td style="padding:18px 32px;background:#f5f1eb;
                               text-align:center;border-top:1px solid #e8dfd5;">
                        <div style="font-size:14px;color:#6b5137;">
                            此為系統自動寄送通知，請勿直接回覆本信件。
                        </div>

                        <div style="margin-top:10px;font-size:12px;color:#9a8d7e;">
                            © 2026 星澄飯店 Grand Aster Hotel. All rights reserved.
                        </div>
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

            System.out.println("會員訂位確認信已加入寄送佇列："
                    + reservation.getContactEmail());
        } catch (Exception e) {
            // 寄信失敗不可影響訂位成立。
            System.err.println("會員訂位確認信發送失敗：" + e.getMessage());
        }
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
