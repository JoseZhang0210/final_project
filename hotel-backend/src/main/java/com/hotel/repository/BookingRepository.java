package com.hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.model.entity.Booking;

/**
 * 訂房資料庫存取層 (Booking Repository)
 */
public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {

    // 依入住日期查詢
    List<Booking> findByCheckInDate(LocalDate checkInDate);

    // 檢查房間是否有未來或當日的預訂
    boolean existsByRoomIdAndCheckOutDateGreaterThanEqual(Integer roomId, LocalDate date);

    // 依訂房狀態查詢
    List<Booking> findByBookingStatus(String bookingStatus);

    // 尋找特定日期區間內，特定房型已經被預訂的房間 ID (排除已取消的訂單)
    @Query("SELECT b.roomId FROM Booking b WHERE b.roomTypeId = :roomTypeId AND b.bookingStatus != '已取消' AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate AND b.roomId IS NOT NULL")
    List<Integer> findBookedRoomIds(
            @Param("roomTypeId") Integer roomTypeId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

    // 查詢特定日期範圍內所有有效訂房（取代全表掃描）
    @Query("SELECT b FROM Booking b WHERE b.roomId IS NOT NULL AND b.checkInDate <= :targetDate AND b.checkOutDate >= :targetDate AND b.bookingStatus NOT IN ('已取消', '已完成')")
    List<Booking> findActiveBookingsForDate(@Param("targetDate") LocalDate targetDate);

    // 檢查特定房間在指定日期是否有待入住或已入住訂單
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.roomId = :roomId AND b.checkInDate <= :targetDate AND b.checkOutDate >= :targetDate AND (b.bookingStatus = '待入住' OR b.bookingStatus = '已入住')")
    boolean hasActiveBookingForRoomOnDate(@Param("roomId") Integer roomId, @Param("targetDate") LocalDate targetDate);

    // 查詢所有未完成且未取消的活躍訂單 (供排程自動推進使用，避免掃描歷史已完成/已取消舊訂單)
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus NOT IN ('已完成', '已取消')")
    List<Booking> findActiveIncompleteBookings();
}
