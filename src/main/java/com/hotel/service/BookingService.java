package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.Booking;
import com.hotel.repository.BookingRepository;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // Create
    public Booking insert(Booking booking) {
        return bookingRepository.save(booking);
    }

    // Read All
    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    // Read by ID
    @Transactional(readOnly = true)
    public Optional<Booking> findById(Integer id) {
        return bookingRepository.findById(id);
    }

    // Delete
    public boolean deleteById(Integer id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
