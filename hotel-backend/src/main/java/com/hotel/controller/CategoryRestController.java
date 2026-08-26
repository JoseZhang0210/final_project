package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // ==============================
    // 查詢全部種類
    // GET /api/categories
    // ==============================

    @GetMapping
    public List<Category> findAllCategories() {

        return categoryRepository.findAll();
    }

    // ==============================
    // 新增種類
    // POST /api/categories
    // ==============================

    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestBody Category category) {

        String categoryName = category.getCategoryName();

        // 空白檢查
        if (categoryName == null
                ||
                categoryName.trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "商品種類名稱不能為空"));
        }

        categoryName = categoryName.trim();

        // 重複檢查
        boolean exists = categoryRepository
                .existsByCategoryNameIgnoreCase(
                        categoryName);

        if (exists) {

            return ResponseEntity
                    .status(
                            HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "message",
                                    "商品種類已存在"));
        }

        category.setCategoryName(
                categoryName);

        Category savedCategory = categoryRepository.save(
                category);

        return ResponseEntity
                .status(
                        HttpStatus.CREATED)
                .body(
                        savedCategory);
    }
}