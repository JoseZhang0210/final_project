package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.model.entity.BookingOrder;

public interface BookingOrderRepository extends JpaRepository<BookingOrder, Integer> {

        List<BookingOrder> findByMemberId(Integer memberId);

        List<BookingOrder> findByOrderStatus(String orderStatus);

        @Query("SELECT b FROM BookingOrder b WHERE " +
                        "(:bookingOrderId IS NULL OR b.bookingOrderId = :bookingOrderId) AND " +
                        "(:memberId IS NULL OR b.memberId = :memberId) AND " +
                        "(:orderStatus IS NULL OR b.orderStatus = :orderStatus)")
        List<BookingOrder> searchOrders(
                        @Param("bookingOrderId") Integer bookingOrderId,
                        @Param("memberId") Integer memberId,
                        @Param("orderStatus") String orderStatus);

        @Modifying
        @Query("DELETE FROM Booking b WHERE b.bookingOrder.bookingOrderId = :bookingOrderId")
        void deleteByBookingOrderId(@Param("bookingOrderId") Integer bookingOrderId);
}
