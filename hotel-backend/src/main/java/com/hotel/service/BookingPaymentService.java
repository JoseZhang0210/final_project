package com.hotel.service;

import java.util.List;
import java.util.Optional;

import com.hotel.model.dto.BookingPaymentDTO;

/**
 * 訂房付款紀錄服務介面 (Booking Payment Service)
 */
public interface BookingPaymentService {
    List<BookingPaymentDTO> findAll();

    Optional<BookingPaymentDTO> findById(Integer paymentId);

    BookingPaymentDTO createPayment(BookingPaymentDTO bookingPaymentDTO);

    BookingPaymentDTO updatePaymentStatus(Integer id, String status);

    BookingPaymentDTO findByBookingId(Integer bookingId);

    BookingPaymentDTO update(Integer id, BookingPaymentDTO dto);

    // 建立或確保結帳時的「待付款」狀態紀錄
    void ensurePendingPayment(Integer bookingId, Integer amount, String paymentMethod);

    // 處理付款成功 (具備冪等性 Idempotent，避免重複付款處理與重複發送通知)
    boolean processSuccessfulPayment(Integer bookingId, Integer amount, String paymentMethod, String transactionId);
}
