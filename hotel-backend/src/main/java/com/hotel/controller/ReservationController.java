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
import com.hotel.service.ReservationService;
import com.hotel.util.MailUtil;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final MailUtil mailUtil;

    public ReservationController(
            ReservationService reservationService,
            MailUtil mailUtil) {

        this.reservationService = reservationService;
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
