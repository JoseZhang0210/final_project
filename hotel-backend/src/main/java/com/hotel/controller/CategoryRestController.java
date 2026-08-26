package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Category;
import com.hotel.repository.CategoryRepository;
import com.hotel.repository.ProductRepository;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryRestController(
            CategoryRepository categoryRepository,
            ProductRepository productRepository) {

        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // =========================================
    // 1. 查詢全部商品種類
    // GET /api/categories
    // =========================================

    @GetMapping
    public List<Category> findAllCategories() {

        return categoryRepository.findAll();
    }

    // =========================================
    // 2. 新增商品種類
    // POST /api/categories
    // =========================================

    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestBody Category category) {

        String categoryName = category.getCategoryName();

        // ==============================
        // 名稱不可為空
        // ==============================

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

        // ==============================
        // 檢查是否重複
        // ==============================

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

        // ==============================
        // ID 交給 SQL Server 自動產生
        // ==============================

        category.setCategoryId(null);

        category.setCategoryName(
                categoryName);

        Category savedCategory = categoryRepository
                .save(category);

        return ResponseEntity
                .status(
                        HttpStatus.CREATED)
                .body(
                        savedCategory);
    }

    // =========================================
    // 3. 修改商品種類
    // PUT /api/categories/{id}
    // =========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Integer id,
            @RequestBody Category formCategory) {

        // ==============================
        // 先找原本分類
        // ==============================

        Category existingCategory = categoryRepository
                .findById(id)
                .orElse(null);

        if (existingCategory == null) {

            return ResponseEntity
                    .status(
                            HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    "找不到商品種類"));
        }

        String categoryName = formCategory
                .getCategoryName();

        // ==============================
        // 名稱不可為空
        // ==============================

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

        // ==============================
        // 找看看是否已有相同名稱
        // ==============================

        Category sameNameCategory = categoryRepository
                .findByCategoryNameIgnoreCase(
                        categoryName)
                .orElse(null);

        // ==============================
        // 如果同名分類存在
        // 而且不是自己
        // 就不能修改
        // ==============================

        if (sameNameCategory != null
                &&
                !sameNameCategory
                        .getCategoryId()
                        .equals(id)) {

            return ResponseEntity
                    .status(
                            HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "message",
                                    "商品種類已存在"));
        }

        // ==============================
        // 更新名稱
        // ==============================

        existingCategory
                .setCategoryName(
                        categoryName);

        Category updatedCategory = categoryRepository
                .save(
                        existingCategory);

        return ResponseEntity
                .ok(
                        updatedCategory);
    }

    // =========================================
    // 4. 刪除商品種類
    // DELETE /api/categories/{id}
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Integer id) {

        // ==============================
        // 查詢分類
        // ==============================

        Category category = categoryRepository
                .findById(id)
                .orElse(null);

        if (category == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    "找不到商品種類"));
        }

        // ==============================
        // 檢查是否有商品正在使用
        // ==============================

        boolean used = productRepository
                .existsByCategoryCategoryId(id);

        if (used) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "message",
                                    "此商品種類仍有商品使用，請先修改商品分類後再刪除"));
        }

        // ==============================
        // 刪除
        // ==============================

        try {

            categoryRepository.delete(category);

            // 強制 Hibernate 現在就執行 DELETE
            // 這樣 FK 錯誤才能在這裡被 catch
            categoryRepository.flush();

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (Exception e) {

            System.err.println(
                    "刪除商品種類失敗："
                            + e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "message",
                                    "此商品種類仍被其他資料使用，無法刪除"));
        }
    }
}