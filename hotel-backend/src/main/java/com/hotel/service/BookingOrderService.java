package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.BookingOrder;
import com.hotel.repository.BookingOrderRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class BookingOrderService {

    private final BookingOrderRepository bookingOrderRepository;

    // 正確注入單一 Repository 即可，級聯刪除由 JPA Cascade 處理
    public BookingOrderService(BookingOrderRepository bookingOrderRepository) {
        this.bookingOrderRepository = bookingOrderRepository;
    }

    // 1. Create - 新增訂單 (直接交給 JPA save 處理)
    public BookingOrder insert(BookingOrder bookingOrder) {
        return bookingOrderRepository.save(bookingOrder);
    }

    // 2. Read All - 查詢所有訂單
    @Transactional(readOnly = true)
    public List<BookingOrder> findAll() {
        return bookingOrderRepository.findAll();
    }

    // 3-1. Read Optional by ID
    @Transactional(readOnly = true)
    public Optional<BookingOrder> findOptionalById(Integer id) {
        return bookingOrderRepository.findById(id);
    }

    // 3-4. 依會員 ID 查詢該會員所有訂單
    @Transactional(readOnly = true)
    public List<BookingOrder> findByMemberId(Integer memberId) {
        return bookingOrderRepository.findByMember_MemberId(memberId);
    }

    // 4. Update - 更新訂單狀態與支付單 (利用 JPA Dirty Checking)
    public BookingOrder update(Integer id, BookingOrder updatedOrder) {
        BookingOrder existingOrder = bookingOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的預訂訂單"));

        // 1. 更新訂單狀態
        if (updatedOrder.getOrderStatus() != null) {
            existingOrder.setOrderStatus(updatedOrder.getOrderStatus());
        }

        // 2. 更新支付關聯 (對應 payment_id)
        // if (updatedOrder.getPayments() != null) {
        // existingOrder.setPayments(updatedOrder.getPayments());
        // }

        // 交易結束時 JPA 會自動進行比對並發送 UPDATE SQL，無須呼叫 save()
        return existingOrder;
    }

    // 5. Delete - 刪除訂單 (依賴 CascadeType.ALL 自動連帶刪除子明細)
    public void deleteById(Integer id) {
        if (!bookingOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的訂單 ID: " + id + " 不存在");
        }
        // Entity 上有 cascade = CascadeType.ALL，刪除主訂單時 JPA 會自動清除 Booking 與 Payment
        bookingOrderRepository.deleteById(id);
    }
}