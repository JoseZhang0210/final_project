package com.hotel.service;

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

    // 1. Create - 新增子預約
    public Booking insert(Booking booking) {
        return bookingRepository.save(booking);
    }

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

    // 3-2. Read by ID (找不到時拋出例外)
    @Transactional(readOnly = true)
    public Booking findById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的預約紀錄"));
    }

    // 3-3. 多條件動態查詢 (改用 JPA 衍生查詢或宣告在 Repository 的方法)
    @Transactional(readOnly = true)
    public List<Booking> search(Integer bookingId, Integer bookingOrderId, String bookingStatus) {
        // 若帶入主鍵，直接單筆精準查詢
        if (bookingId != null) {
            return bookingRepository.findById(bookingId)
                    .map(List::of)
                    .orElse(List.of());
        }

        // 依據條件情境改呼叫 Repository 衍生方法
        if (bookingOrderId != null && bookingStatus != null) {
            return bookingRepository.findByBookingOrder_BookingOrderIdAndBookingStatus(bookingOrderId, bookingStatus);
        } else if (bookingOrderId != null) {
            return bookingRepository.findByBookingOrder_BookingOrderId(bookingOrderId);
        } else if (bookingStatus != null && !bookingStatus.trim().isEmpty()) {
            return bookingRepository.findByBookingStatus(bookingStatus);
        }

        return bookingRepository.findAll();
    }

    // 4. Update - 修改預約資料 (利用 Dirty Checking 自動更新)
    public Booking updateBooking(Integer id, Booking newBookingData) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("修改失敗：找不到 ID 為 " + id + " 的預訂資料"));

        if (newBookingData.getCheckInDate() != null) {
            existingBooking.setCheckInDate(newBookingData.getCheckInDate());
        }
        if (newBookingData.getCheckOutDate() != null) {
            existingBooking.setCheckOutDate(newBookingData.getCheckOutDate());
        }
        if (newBookingData.getBookingStatus() != null) {
            existingBooking.setBookingStatus(newBookingData.getBookingStatus());
        }
        if (newBookingData.getBookingPrice() != null) {
            existingBooking.setBookingPrice(newBookingData.getBookingPrice());
        }
        if (newBookingData.getGuestNum() != null) {
            existingBooking.setGuestNum(newBookingData.getGuestNum());
        }

        // 修正：依 Entity 定義更新關聯物件，而非不存在的 Id 屬性
        if (newBookingData.getRoomType() != null) {
            existingBooking.setRoomType(newBookingData.getRoomType());
        }
        if (newBookingData.getRoom() != null) {
            existingBooking.setRoom(newBookingData.getRoom());
        }

        // 交易結束時 JPA 會自動進行比對並發送 Update SQL，無需呼叫 save()
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