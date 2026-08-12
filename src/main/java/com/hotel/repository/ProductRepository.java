package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Product;

public interface ProductRepository
                extends JpaRepository<Product, Integer> {
        List<Product> findByStatusOrderByProductIdAsc(String status);
}