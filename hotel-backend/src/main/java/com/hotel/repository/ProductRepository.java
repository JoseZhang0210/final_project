package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Product;

public interface ProductRepository
        extends JpaRepository<Product, Integer> {
}