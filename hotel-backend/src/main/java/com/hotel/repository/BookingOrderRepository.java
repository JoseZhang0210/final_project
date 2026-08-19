package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.BookingOrder;

public interface BookingOrderRepository extends JpaRepository<BookingOrder, Integer> {


}
