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

import com.hotel.model.entity.BookingOrder;
import com.hotel.service.BookingOrderService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/booking-orders")
public class BookingOrderController {

    private final BookingOrderService bookingOrderService;

    public BookingOrderController(BookingOrderService bookingOrderService) {
        this.bookingOrderService = bookingOrderService;
    }

    // 1. 條件搜尋訂單列表 (GET
    // /api/booking-orders?bookingOrderId=...&memberId=...&orderStatus=...)
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

    // 2. 依 ID 查詢單筆訂單 (GET /api/booking-orders/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingOrderById(@PathVariable Integer id) {
        try {
            BookingOrder bookingOrder = bookingOrderService.findById(id);
            return ResponseEntity.ok(bookingOrder);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "系統錯誤：" + e.getMessage()));
        }
    }

    // 3. 新增預訂訂單 (POST /api/booking-orders)
    @PostMapping
    public ResponseEntity<?> createBookingOrder(@RequestBody BookingOrder bookingOrder) {
        try {
            // 確保新增時 ID 為空，由資料庫自動遞增
            bookingOrder.setBookingOrderId(null);

            BookingOrder savedOrder = bookingOrderService.insert(bookingOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("FK_")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "外鍵約束錯誤：請確認關聯的會員 ID 是否存在！"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "新增訂單失敗：" + e.getMessage()));
        }
    }

    // 4. 更新預訂訂單 (PUT /api/booking-orders/{id})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBookingOrder(@PathVariable Integer id, @RequestBody BookingOrder bookingOrder) {
        try {
            BookingOrder updatedOrder = bookingOrderService.update(id, bookingOrder);
            return ResponseEntity.ok(updatedOrder);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
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
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "刪除失敗：該訂單可能已被其他資料關聯或無法被級聯刪除！"));
        }
    }
}