package com.hotel.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.BookingOrder;
import com.hotel.repository.BookingOrderRepository;
import com.hotel.repository.BookingRepository;

@Service
@Transactional
public class BookingOrderService {

    private final BookingOrderRepository bookingOrderRepository;

    private final BookingRepository bookingRepository;

    public BookingOrderService(BookingOrderRepository bookingOrderRepository) {
        this.bookingOrderRepository = bookingOrderRepository;
        this.bookingRepository = null;
    }

    // 1. Create - 新增訂單
    @Transactional
    public BookingOrder insert(BookingOrder bookingOrder) {
        if (bookingOrder.getCreatedAt() == null) {
            bookingOrder.setCreatedAt(LocalDateTime.now());
        }
        // 依據 Entity 註解，預設狀態建議使用英文標籤 (例如 PAID 或 PENDING)
        if (bookingOrder.getOrderStatus() == null) {
            bookingOrder.setOrderStatus("已付款");
        }

        return bookingOrderRepository.save(bookingOrder);
    }

    // 2. Read All - 查詢所有訂單
    @Transactional(readOnly = true)
    public List<BookingOrder> findAll() {
        return bookingOrderRepository.findAll();
    }

    // 3. Read by ID - 依 ID 查詢
    @Transactional(readOnly = true)
    public BookingOrder findById(Integer id) {
        return bookingOrderRepository.findById(id).orElse(null);
    }

    // 搜尋訂單
    @Transactional(readOnly = true)
    public List<BookingOrder> search(Integer bookingOrderId, Integer memberId, String orderStatus) {
        return bookingOrderRepository.searchOrders(bookingOrderId, memberId, orderStatus);
    }

    // 4. Update - 更新訂單
    @Transactional
    public BookingOrder update(Integer id, BookingOrder updatedOrder) {
        return bookingOrderRepository.findById(id)
                .map(existingOrder -> {
                    if (updatedOrder.getMemberId() != null) {
                        existingOrder.setMemberId(updatedOrder.getMemberId());
                    }
                    if (updatedOrder.getBookingTotalPrice() != null) {
                        existingOrder.setBookingTotalPrice(updatedOrder.getBookingTotalPrice());
                    }
                    if (updatedOrder.getOrderStatus() != null) {
                        existingOrder.setOrderStatus(updatedOrder.getOrderStatus());
                    }
                    if (updatedOrder.getPaymentId() != null) {
                        existingOrder.setPaymentId(updatedOrder.getPaymentId());
                    }

                    return bookingOrderRepository.save(existingOrder);
                })
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的預訂訂單"));
    }

    // 5. Delete - 刪除訂單
    @Transactional
    public void deleteById(Integer id) {
        // 1. 檢查主訂單是否存在
        if (!bookingOrderRepository.existsById(id)) {
            throw new RuntimeException("找不到 ID 為 " + id + " 的預訂訂單，無法刪除");
        }

        // 2. 先刪除關聯的子資料（如 Booking 明細），避免外鍵約束 (Foreign Key Constraint) 報錯
        bookingRepository.deleteByBookingOrderId(id);

        // 3. 再刪除主訂單
        bookingOrderRepository.deleteById(id);
    }
}