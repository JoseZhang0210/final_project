package com.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Category;

public interface CategoryRepository
        extends JpaRepository<Category, Integer> {

    boolean existsByCategoryNameIgnoreCase(
            String categoryName);

    Optional<Category> findByCategoryNameIgnoreCase(
            String categoryName);
}