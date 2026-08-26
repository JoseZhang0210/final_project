package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Product;

public interface ProductRepository
        extends JpaRepository<Product, Integer> {

    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    boolean existsByCategoryCategoryId(
            Integer categoryId);
}