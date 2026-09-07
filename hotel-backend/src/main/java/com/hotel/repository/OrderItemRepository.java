package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.OrderItem;
import com.hotel.model.entity.OrderItemId;

public interface OrderItemRepository
                extends JpaRepository<OrderItem, OrderItemId> {

        List<OrderItem> findByOrderId(Integer orderId);
}