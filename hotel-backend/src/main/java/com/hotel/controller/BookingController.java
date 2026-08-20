package com.hotel.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.entity.Booking;
import com.hotel.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // 1. 條件查詢 (GET
    // /api/bookings?bookingId=...&bookingOrderId=...&bookingStatus=...)
    @GetMapping
    public ResponseEntity<?> getBookings(
            @RequestParam(required = false) Integer bookingId,
            @RequestParam(required = false) Integer bookingOrderId,
            @RequestParam(required = false) String bookingStatus) {
        try {
            List<Booking> list = bookingService.search(bookingId, bookingOrderId, bookingStatus);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "查詢失敗：" + e.getMessage()));
        }
    }

    // 2. 單筆查詢 (GET /api/bookings/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer id) {
        try {
            Optional<Booking> booking = bookingService.findById(id);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "找不到 ID 為 " + id + " 的預訂紀錄"));
            }
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "查詢失敗：" + e.getMessage()));
        }
    }

    // 3. 新增預訂 (POST /api/bookings)
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        try {
            // 依據 Entity 註解規範，未帶入狀態時預設為 PENDING
            if (booking.getBookingStatus() == null || booking.getBookingStatus().isBlank()) {
                booking.setBookingStatus("PENDING");
            }

            Booking savedBooking = bookingService.insert(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "新增預訂失敗：" + e.getMessage()));
        }
    }

    // 4. 更新預訂 (PUT /api/bookings/{id})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Integer id, @RequestBody Booking booking) {
        try {
            booking.setBookingId(id);
            Booking updatedBooking = bookingService.updateBooking(id, booking);
            return ResponseEntity.ok(updatedBooking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "更新失敗：" + e.getMessage()));
        }
    }

    // 5. 刪除預訂 (DELETE /api/bookings/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Integer id) {
        try {
            boolean deleted = bookingService.deleteById(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "預訂已成功刪除！"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "刪除失敗：該預訂不存在！"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "刪除失敗：該預訂可能已被其他資料關聯或不存在！"));
        }
    }
}