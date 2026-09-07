package com.hotel.service;

import java.util.List;
import java.util.Optional;

import com.hotel.model.dto.BookingPaymentDTO;

public interface BookingPaymentService {
    List<BookingPaymentDTO> findAll();
    Optional<BookingPaymentDTO> findById(Integer paymentId);
    BookingPaymentDTO createPayment(BookingPaymentDTO bookingPaymentDTO);
    BookingPaymentDTO updatePaymentStatus(Integer id, String status);
    BookingPaymentDTO findByBookingId(Integer bookingId);
    BookingPaymentDTO update(Integer id, BookingPaymentDTO dto);
}
