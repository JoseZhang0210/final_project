package com.hotel.service;

import java.util.List;
import java.util.Optional;

import com.hotel.model.entity.OrderItem;
import com.hotel.model.entity.Product;

/**
 * OrderItemRepository
 */
public interface OrderItemRepository {

    void save(OrderItem item);

    List<OrderItem> findByOrderId(Integer orderId);

    Optional<Product> findById(Integer orderItemId);

    void save(Product item);

    void delete(Product item);

    void deleteAll();

}
