package com.hotel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.Booking;
import com.hotel.repository.BookingRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // // 1. Create - 新增子預約
    // public Booking insert(Booking booking) {
    // return bookingRepository.save(booking);
    // }

    // 2. Read All - 查詢所有預約
    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    // 3-1. Read Optional by ID
    @Transactional(readOnly = true)
    public Optional<Booking> findOptionalById(Integer id) {
        return bookingRepository.findById(id);
    }

    // // 3-2. Read by ID (找不到時拋出例外)
    // @Transactional(readOnly = true)
    // public Booking findById(Integer id) {
    // return bookingRepository.findById(id)
    // .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的預約紀錄"));
    // }
    // 1. 依 Booking ID 查詢 (回傳 Optional)
    @Transactional(readOnly = true)
    public Optional<Booking> findById(Integer bookingId) {
        return bookingRepository.findById(bookingId);
    }

    // 2. 依入住日期查詢
    @Transactional(readOnly = true)
    public List<Booking> findByCheckInDate(LocalDate checkInDate) {
        return bookingRepository.findByCheckInDate(checkInDate);
    }

    // 3. 依訂房狀態查詢
    @Transactional(readOnly = true)
    public List<Booking> findByBookingStatus(String bookingStatus) {
        return bookingRepository.findByBookingStatus(bookingStatus);
    }

    // 4. Update - 修改預約資料 (利用 Dirty Checking 自動更新)
    public Booking updateBooking(Integer id, Booking newBookingData) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("修改失敗：找不到 ID 為 " + id + " 的預訂資料"));

        // 1. 入住時間
        if (newBookingData.getCheckInDate() != null) {
            existingBooking.setCheckInDate(newBookingData.getCheckInDate());
        }

        // 2. 退房時間
        if (newBookingData.getCheckOutDate() != null) {
            existingBooking.setCheckOutDate(newBookingData.getCheckOutDate());
        }

        // 3. 入住人數 (guest_number)
        if (newBookingData.getGuestNum() != null) {
            existingBooking.setGuestNum(newBookingData.getGuestNum());
        }

        // 4. 預約狀態
        if (newBookingData.getBookingStatus() != null) {
            existingBooking.setBookingStatus(newBookingData.getBookingStatus());
        }

        // 交易結束時 JPA 會自動進行比對並發送 UPDATE SQL，無需呼叫 save()
        return existingBooking;
    }

    // 5. Delete - 刪除預約
    public void deleteById(Integer id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的預約 ID: " + id + " 不存在");
        }
        bookingRepository.deleteById(id);
    }

}