package com.hotel.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.entity.Booking;
import com.hotel.entity.BookingOrder;
import com.hotel.service.BookingOrderService;

@RestController
@RequestMapping("/api/booking-orders")
@CrossOrigin(origins = "*")
public class BookingOrderController {

    private final BookingOrderService bookingOrderService;

    public BookingOrderController(BookingOrderService bookingOrderService) {
        this.bookingOrderService = bookingOrderService;
    }

    // 1. 取得所有預訂主訂單 (GET /api/booking-orders)
    @GetMapping
    public ResponseEntity<?> getAllBookingOrders() {
        try {
            List<BookingOrder> list = bookingOrderService.findAll();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "無法取得訂單列表：" + e.getMessage()));
        }
    }

    // 2. 取得單筆預訂主訂單 (GET /api/booking-orders/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingOrderById(@PathVariable Integer id) {
        try {
            BookingOrder bookingOrder = bookingOrderService.findById(id);
            if (bookingOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "找不到該訂單資料"));
            }
            return ResponseEntity.ok(bookingOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "查詢失敗：" + e.getMessage()));
        }
    }

    // 3. 新增預訂訂單 (POST /api/booking-orders)
    @PostMapping
    public ResponseEntity<?> createBookingOrder(@RequestBody BookingOrder bookingOrder) {
        try {
            prepareBookingOrder(bookingOrder);
            BookingOrder savedOrder = bookingOrderService.insert(bookingOrder);
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "新增訂單失敗：" + e.getMessage()));
        }
    }

    // 4. 更新預訂訂單 (PUT /api/booking-orders/{id})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBookingOrder(@PathVariable Integer id, @RequestBody BookingOrder bookingOrder) {
        try {
            bookingOrder.setBookingOrderId(id);
            prepareBookingOrder(bookingOrder);

            BookingOrder updatedOrder = bookingOrderService.update(id, bookingOrder);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "更新訂單失敗：" + e.getMessage()));
        }
    }

    // 5. 刪除預訂訂單 (DELETE /api/booking-orders/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookingOrder(@PathVariable Integer id) {
        try {
            bookingOrderService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "訂單已成功刪除！"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "刪除失敗：該訂單可能已被其他資料關聯或不存在！"));
        }
    }

    /**
     * 輔助方法：處理時間設定與 JPA 雙向關聯外鍵
     */
    private void prepareBookingOrder(BookingOrder bookingOrder) {
        if (bookingOrder.getCreatedAt() == null) {
            bookingOrder.setCreatedAt(new Date());
        }

        if (bookingOrder.getBookings() != null) {
            List<Booking> validBookings = bookingOrder.getBookings().stream()
                    .filter(b -> b.getCheckInDate() != null && b.getCheckOutDate() != null)
                    .peek(b -> b.setBookingOrder(bookingOrder)) // 確保每一個子 Booking 都綁定父層物件
                    .collect(Collectors.toList());

            bookingOrder.getBookings().clear();
            bookingOrder.getBookings().addAll(validBookings);
        }
    }
}