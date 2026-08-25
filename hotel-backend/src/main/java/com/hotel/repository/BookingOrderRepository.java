package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.BookingOrder;

public interface BookingOrderRepository extends JpaRepository<BookingOrder, Integer> {

        List<BookingOrder> findByMember_MemberId(Integer memberId);
}
