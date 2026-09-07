package com.hotel.service;

import java.util.List;
import java.util.Optional;

import com.hotel.model.dto.BookingDTO;

public interface BookingService {
    List<BookingDTO> findAll();
    Optional<BookingDTO> findById(Integer bookingId);
    List<BookingDTO> searchByCriteria(BookingDTO criteria);
    BookingDTO updateBooking(Integer id, BookingDTO newBookingData);
    void deleteById(Integer id);
    BookingDTO insert(BookingDTO bookingDTO);
    Integer calculateBookingPrice(Integer roomTypeId, java.time.LocalDate checkInDate, java.time.LocalDate checkOutDate);
}