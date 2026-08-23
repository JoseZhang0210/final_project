package com.hotel.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Category;
import com.hotel.repository.CategoryRepository;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryRepository categoryRepository;

    public CategoryRestController(
            CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Category> findAllCategories() {

        return categoryRepository.findAll();
    }
}