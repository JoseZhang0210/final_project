package com.hotel.controller;

import java.util.List;
import java.util.Map;

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

import com.hotel.entity.BookingOrder;
import com.hotel.service.BookingOrderService;

@RestController
@RequestMapping("/api/booking-orders")
public class BookingOrderController {

    private final BookingOrderService bookingOrderService;

    public BookingOrderController(BookingOrderService bookingOrderService) {
        this.bookingOrderService = bookingOrderService;
    }

    @GetMapping
    public ResponseEntity<?> getBookingOrders(
            @RequestParam(required = false) Integer bookingOrderId,
            @RequestParam(required = false) Integer memberId,
            @RequestParam(required = false) String orderStatus) {
        try {
            List<BookingOrder> list = bookingOrderService.search(bookingOrderId, memberId, orderStatus);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "查詢失敗：" + e.getMessage()));
        }
    }

    // 3. 新增預訂訂單 (POST /api/booking-orders)
    @PostMapping
    public ResponseEntity<?> createBookingOrder(@RequestBody BookingOrder bookingOrder) {
        try {
            // 純 FK 模式下，不需做複雜的 entity 補值 (prepareBookingOrder)，直接 insert
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
     * //
     */
    // private void prepareBookingOrder(BookingOrder bookingOrder) {
    // if (bookingOrder.getCreatedAt() == null) {
    // bookingOrder.setCreatedAt(LocalDateTime.now());
    // }

    // if (bookingOrder.getBookings() != null) {
    // List<Booking> validBookings = bookingOrder.getBookings().stream()
    // .filter(b -> b.getCheckInDate() != null && b.getCheckOutDate() != null)
    // .peek(b -> b.setBookingOrder(bookingOrder)) // 確保每一個子 Booking 都綁定父層物件
    // .collect(Collectors.toList());

    // bookingOrder.getBookings().clear();
    // bookingOrder.getBookings().addAll(validBookings);
    // }
    // }
}