package com.hotel.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.BookingOrder;
import com.hotel.repository.BookingOrderRepository;

@Service
@Transactional
public class BookingOrderService {

    private final BookingOrderRepository bookingOrderRepository;

    public BookingOrderService(BookingOrderRepository bookingOrderRepository) {
        this.bookingOrderRepository = bookingOrderRepository;
    }

    // 1. Create - 新增訂單
    public BookingOrder insert(BookingOrder bookingOrder) {
        if (bookingOrder.getCreatedAt() == null) {
            bookingOrder.setCreatedAt(new Date());
        }
        return bookingOrderRepository.save(bookingOrder);
    }

    // 2. Read All - 查詢所有訂單
    @Transactional(readOnly = true)
    public List<BookingOrder> findAll() {
        return bookingOrderRepository.findAll();
    }

    // 3. Read by ID - 根據 ID 查詢單筆訂單
    @Transactional(readOnly = true)
    public BookingOrder findById(Integer id) {
        return bookingOrderRepository.findById(id).orElse(null);
    }

    // 4. Update - 更新訂單內容
    public BookingOrder update(Integer id, BookingOrder updatedOrder) {
        return bookingOrderRepository.findById(id)
                .map(existingOrder -> {
                    // 更新基本欄位
                    if (updatedOrder.getMemberId() != null) {
                        existingOrder.setMemberId(updatedOrder.getMemberId());
                    }
                    if (updatedOrder.getBookingTotalPrice() != null) {
                        existingOrder.setBookingTotalPrice(updatedOrder.getBookingTotalPrice());
                    }
                    if (updatedOrder.getOrderStatus() != null) {
                        existingOrder.setOrderStatus(updatedOrder.getOrderStatus());
                    }

                    // paymentId 允許更新（包含清空為 null）
                    existingOrder.setPaymentId(updatedOrder.getPaymentId());

                    // 若前端有帶入建立時間則更新，否則保留原建立時間
                    if (updatedOrder.getCreatedAt() != null) {
                        existingOrder.setCreatedAt(updatedOrder.getCreatedAt());
                    }

                    // 處理一對多明細 bookings 集合更新（維護 JPA 關聯）
                    if (updatedOrder.getBookings() != null) {
                        existingOrder.getBookings().clear();
                        updatedOrder.getBookings().forEach(booking -> {
                            // 修復重點：應設定關聯的 BookingOrder 物件，而非 setBookingId
                            booking.setBookingOrder(existingOrder);
                            existingOrder.getBookings().add(booking);
                        });
                    }

                    return bookingOrderRepository.save(existingOrder);
                })
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的預訂訂單"));
    }

    // 5. Delete - 根據 ID 刪除訂單
    public void deleteById(Integer id) {
        if (!bookingOrderRepository.existsById(id)) {
            throw new RuntimeException("找不到 ID 為 " + id + " 的預訂訂單，無法刪除");
        }
        bookingOrderRepository.deleteById(id);
    }

    // 6. 備用新增方法（內部統一呼叫 insert 避免冗餘）
    public BookingOrder createBookingOrder(BookingOrder order) {
        return insert(order);
    }
}