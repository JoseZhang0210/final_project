package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.model.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

        @Query("SELECT b FROM Booking b WHERE (:bookingId IS NULL OR b.bookingId = :bookingId) AND " +
                        "(:bookingOrderId IS NULL OR b.bookingOrder.bookingOrderId = :bookingOrderId) AND " +
                        "(:bookingStatus IS NULL OR b.bookingStatus LIKE CONCAT('%', :bookingStatus, '%'))")
        List<Booking> search(
                        @Param("bookingId") Integer bookingId,
                        @Param("bookingOrderId") Integer bookingOrderId,
                        @Param("bookingStatus") String bookingStatus);

        // 使用底線 _ 避開名稱歧義，代表 bookingOrder 裡面的 bookingOrderId 屬性
        void deleteByBookingOrder_BookingOrderId(Integer bookingOrderId);

        // 依主訂單 ID 查詢
        List<Booking> findByBookingOrder_BookingOrderId(Integer bookingOrderId);

        // 依狀態查詢
        List<Booking> findByBookingStatus(String bookingStatus);

        // 依主訂單 ID 與狀態雙重條件查詢
        List<Booking> findByBookingOrder_BookingOrderIdAndBookingStatus(Integer bookingOrderId, String bookingStatus);

}
