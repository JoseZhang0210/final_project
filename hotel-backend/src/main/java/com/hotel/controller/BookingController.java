package com.hotel.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Booking;
import com.hotel.service.BookingService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAll());
    }

    // 1. 依 ID 查詢 (GET /api/bookings/10)
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return bookingService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "找不到 ID 為 " + id + " 的預訂紀錄")));
    }

    // 2. 依入住日期查詢 (GET /api/bookings/check-in?date=2026-08-25)
    @GetMapping("/check-in")
    public ResponseEntity<List<Booking>> getByCheckInDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingService.findByCheckInDate(date));
    }

    // 3. 依狀態查詢 (GET /api/bookings/status?status=已確認)
    @GetMapping("/status")
    public ResponseEntity<List<Booking>> getByStatus(@RequestParam String status) {
        return ResponseEntity.ok(bookingService.findByBookingStatus(status));
    }

    // 4. 更新預訂 (PUT /api/bookings/{id})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Integer id, @RequestBody Booking booking) {
        try {
            Booking updatedBooking = bookingService.updateBooking(id, booking);
            return ResponseEntity.ok(updatedBooking);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "更新失敗：" + e.getMessage()));
        }
    }

    // 5. 刪除預訂 (DELETE /api/bookings/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Integer id) {
        try {
            bookingService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "預訂已成功刪除！"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "刪除失敗：該預訂可能已被其他資料關聯！"));
        }
    }
}