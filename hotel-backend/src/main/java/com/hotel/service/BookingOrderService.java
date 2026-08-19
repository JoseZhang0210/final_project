package com.hotel.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.Booking;
import com.hotel.entity.BookingOrder;
import com.hotel.entity.Room;
import com.hotel.repository.BookingOrderRepository;
import com.hotel.repository.RoomRepository;

@Service
@Transactional
public class BookingOrderService {

    private final BookingOrderRepository bookingOrderRepository;
    private final RoomRepository roomRepository;

    public BookingOrderService(BookingOrderRepository bookingOrderRepository, RoomRepository roomRepository) {
        this.bookingOrderRepository = bookingOrderRepository;
        this.roomRepository = roomRepository;
    }

    // ==========================================
    // 核心業務 1：按下 Confirm 鎖房（防搶房與扣庫存）
    // ==========================================
    // public BookingOrder confirmAndLockRoom(BookingOrder bookingOrder, Integer
    // roomTypeId) {
    // if (bookingOrder.getBookings() == null ||
    // bookingOrder.getBookings().isEmpty()) {
    // throw new RuntimeException("預訂失敗：未選擇任何房間明細");
    // }

    // // 取出預訂的入住與退房日期
    // Booking bookingItem = bookingOrder.getBookings().get(0);
    // LocalDateTime fifteenMinsAgo = LocalDateTime.now().minusMinutes(15);

    // // 1. 動態查詢可用空房（SQL 自動扣除 PAID 與 15分鐘內 PENDING 的房間）
    // List<Room> availableRooms = roomRepository.findAvailableRooms(
    // roomTypeId,
    // bookingItem.getCheckInDate(),
    // bookingItem.getCheckOutDate(),
    // fifteenMinsAgo
    // );

    // // 2. 庫存檢查：若無可用空房則阻擋
    // if (availableRooms.isEmpty()) {
    // throw new RuntimeException("手腳太慢了！該房型在此時段已無空房可供預訂");
    // }

    // // 3. 綁定分配到的實體房間，並設定鎖房起算時間與 PENDING 狀態
    // bookingOrder.setCreatedAt(LocalDateTime.now());
    // bookingOrder.setOrderStatus("PENDING");

    // bookingOrder.getBookings().forEach(b -> {
    // b.setRoom(availableRooms.get(0)); // 綁定空房
    // b.setBookingStatus("PENDING");
    // b.setBookingOrder(bookingOrder);
    // });

    // return bookingOrderRepository.save(bookingOrder);
    // }

    // ==========================================
    // 核心業務 2：確認刷卡付款（15 分鐘校驗）
    // ==========================================
    public BookingOrder processPayment(Integer id, Integer paymentId) {
        BookingOrder order = bookingOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的預訂訂單"));

        // 以「秒」精確計算是否超時（15 分鐘 = 900 秒）
        long secondsPassed = Duration.between(order.getCreatedAt(), LocalDateTime.now()).getSeconds();

        if (secondsPassed >= 900 || "EXPIRED".equals(order.getOrderStatus())) {
            order.setOrderStatus("EXPIRED");
            bookingOrderRepository.save(order);
            throw new RuntimeException("訂單已超過 15 分鐘付款期限，房間已被自動釋放");
        }

        // 付款成功：狀態轉為 PAID，正式鎖定該房間庫存
        order.setOrderStatus("PAID");
        order.setPaymentId(paymentId);

        if (order.getBookings() != null) {
            order.getBookings().forEach(booking -> booking.setBookingStatus("CONFIRMED"));
        }

        return bookingOrderRepository.save(order);
    }

    // ==========================================
    // 基礎 CRUD 方法
    // ==========================================

    // 1. Create - 通用新增
    public BookingOrder insert(BookingOrder bookingOrder) {
        if (bookingOrder.getCreatedAt() == null) {
            bookingOrder.setCreatedAt(LocalDateTime.now());
        }
        if (bookingOrder.getOrderStatus() == null) {
            bookingOrder.setOrderStatus("PENDING");
        }
        if (bookingOrder.getBookings() != null && !bookingOrder.getBookings().isEmpty()) {
            bookingOrder.getBookings().forEach(booking -> booking.setBookingOrder(bookingOrder));
        }
        return bookingOrderRepository.save(bookingOrder);
    }

    // 2. Read All - 查詢所有
    @Transactional(readOnly = true)
    public List<BookingOrder> findAll() {
        return bookingOrderRepository.findAll();
    }

    // 3. Read by ID - 依 ID 查詢
    @Transactional(readOnly = true)
    public BookingOrder findById(Integer id) {
        return bookingOrderRepository.findById(id).orElse(null);
    }

    // 4. Update - 更新訂單內容
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

                    existingOrder.setPaymentId(updatedOrder.getPaymentId());

                    if (updatedOrder.getBookings() != null) {
                        existingOrder.getBookings().clear();
                        updatedOrder.getBookings().forEach(existingOrder::addBooking);
                    }

                    return bookingOrderRepository.save(existingOrder);
                })
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的預訂訂單"));
    }

    // 5. Delete - 刪除訂單
    public void deleteById(Integer id) {
        if (!bookingOrderRepository.existsById(id)) {
            throw new RuntimeException("找不到 ID 為 " + id + " 的預訂訂單，無法刪除");
        }
        bookingOrderRepository.deleteById(id);
    }
}