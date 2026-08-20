package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

    public List<Booking> search(Integer bookingId, Integer bookingOrderId, String bookingStatus) {
        Booking probe = new Booking();

        // 1. 設定主鍵 ID
        if (bookingId != null) {
            probe.setBookingId(bookingId);
        }

        // 2. 設定純 FK (Integer)
        if (bookingOrderId != null) {
            probe.setBookingOrderId(bookingOrderId); // 直接傳入 Integer 即可！
        }

        // 3. 設定訂單狀態
        if (bookingStatus != null && !bookingStatus.trim().isEmpty()) {
            probe.setBookingStatus(bookingStatus);
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);

        return bookingRepository.findAll(Example.of(probe, matcher));
    }

    public Booking updateBooking(Integer id, Booking newBookingData) {
        // 1. 尋找既有資料
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("修改失敗：找不到 ID 為 " + id + " 的預訂資料"));

        // 2. 選擇性更新欄位
        existingBooking.setCheckInDate(newBookingData.getCheckInDate());
        existingBooking.setCheckOutDate(newBookingData.getCheckOutDate());
        existingBooking.setRoomTypeId(newBookingData.getRoomTypeId());
        existingBooking.setBookingStatus(newBookingData.getBookingStatus());

        // 3. 儲存
        return bookingRepository.save(existingBooking);
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
