package com.hotel.controller;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.dto.ApiResponse;
import com.hotel.entity.Booking;
import com.hotel.entity.BookingOrder;
import com.hotel.service.BookingOrderService;

/**
 * 預訂訂單 API 控制器
 * 所有方法返回 JSON 格式的 ApiResponse 資料
 */
@RestController
@RequestMapping("/api/booking-orders")
public class BookingOrderController {

    private final BookingOrderService bookingOrderService;

    public BookingOrderController(BookingOrderService bookingOrderService) {
        this.bookingOrderService = bookingOrderService;
    }

    /**
     * 獲取所有預訂訂單列表
     * 
     * @return 預訂訂單列表的 JSON 回應
     */
    @GetMapping
    public ApiResponse<List<BookingOrder>> getAllBookingOrders() {
        List<BookingOrder> orders = bookingOrderService.findAll();
        return ApiResponse.success(orders, "預訂訂單列表載入成功");
    }

    /**
     * 獲取單個預訂訂單詳情
     * 
     * @param id 訂單 ID
     * @return 訂單詳情的 JSON 回應
     */
    @GetMapping("/{id}")
    public ApiResponse<BookingOrder> getBookingOrderById(@PathVariable Integer id) {
        BookingOrder order = bookingOrderService.findById(id).orElse(null);
        if (order == null) {
            return ApiResponse.error(404, "訂單不存在");
        }
        return ApiResponse.success(order, "訂單詳情載入成功");
    }

    /**
     * 建立新預訂訂單
     * 
     * @param bookingOrder 訂單資訊 (JSON 請求體)
     * @return 建立結果的 JSON 回應
     */
    @PostMapping
    public ApiResponse<BookingOrder> createBookingOrder(@RequestBody BookingOrder bookingOrder) {
        try {
            if (bookingOrder.getCreatedAt() == null) {
                bookingOrder.setCreatedAt(new Date());
            }
            // 建立雙向關聯
            if (bookingOrder.getBookings() != null) {
                for (Booking booking : bookingOrder.getBookings()) {
                    booking.setBookingOrder(bookingOrder);
                }
            }
            BookingOrder savedOrder = bookingOrderService.insert(bookingOrder);
            return ApiResponse.success(savedOrder, "預訂訂單建立成功");
        } catch (Exception e) {
            return ApiResponse.error(400, "訂單建立失敗: " + e.getMessage());
        }
    }

    /**
     * 更新預訂訂單
     * 
     * @param id           訂單 ID
     * @param bookingOrder 更新的訂單資訊 (JSON 請求體)
     * @return 更新結果的 JSON 回應
     */
    @PutMapping("/{id}")
    public ApiResponse<BookingOrder> updateBookingOrder(
            @PathVariable Integer id,
            @RequestBody BookingOrder bookingOrder) {
        try {
            // 建立雙向關聯
            if (bookingOrder.getBookings() != null) {
                for (Booking booking : bookingOrder.getBookings()) {
                    booking.setBookingOrder(bookingOrder);
                }
            }
            BookingOrder updatedOrder = bookingOrderService.update(id, bookingOrder);
            return ApiResponse.success(updatedOrder, "預訂訂單更新成功");
        } catch (Exception e) {
            return ApiResponse.error(400, "訂單更新失敗: " + e.getMessage());
        }
    }

    /**
     * 刪除預訂訂單
     * 
     * @param id 訂單 ID
     * @return 刪除結果的 JSON 回應
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBookingOrder(@PathVariable Integer id) {
        try {
            BookingOrder order = bookingOrderService.findById(id).orElse(null);
            if (order == null) {
                return ApiResponse.error(404, "訂單不存在");
            }
            bookingOrderService.deleteById(id);
            return ApiResponse.success("訂單已刪除", "預訂訂單刪除成功");
        } catch (Exception e) {
            return ApiResponse.error(400, "訂單刪除失敗: " + e.getMessage());
        }
    }
}