package com.hotel.controller;

import org.springframework.web.bind.annotation.*;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.ProductDTO;
import com.hotel.entity.Category;
import com.hotel.entity.Product;
import com.hotel.repository.CategoryRepository;
import com.hotel.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 產品控制器 - 轉換為 REST API
 * 所有方法返回 JSON 格式的 ApiResponse 資料
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

        private final ProductService productService;
        private final CategoryRepository categoryRepository;

        public ProductController(
                        ProductService productService,
                        CategoryRepository categoryRepository) {
                this.productService = productService;
                this.categoryRepository = categoryRepository;
        }

        /**
         * 獲取所有產品列表
         * 
         * @return 產品列表的 JSON 回應
         */
        @GetMapping
        public ApiResponse<List<ProductDTO>> getAllProducts() {
                List<Product> products = productService.findAllProducts();
                List<ProductDTO> dtos = products.stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
                return ApiResponse.success(dtos, "產品列表載入成功");
        }

        /**
         * 獲取單個產品詳情
         * 
         * @param id 產品 ID
         * @return 產品詳情的 JSON 回應
         */
        @GetMapping("/{id}")
        public ApiResponse<ProductDTO> getProductById(@PathVariable Integer id) {
                Product product = productService.findById(id);
                if (product == null) {
                        return ApiResponse.error(404, "產品不存在");
                }
                return ApiResponse.success(convertToDTO(product), "產品詳情載入成功");
        }

        /**
         * 建立新產品
         * 
         * @param product 產品資訊 (JSON 請求體)
         * @return 建立結果的 JSON 回應
         */
        @PostMapping
        public ApiResponse<ProductDTO> createProduct(@RequestBody Product product) {
                try {
                        if (product.getStatus() == null || product.getStatus().isEmpty()) {
                                product.setStatus("ACTIVE");
                        }
                        Product savedProduct = productService.save(product);
                        return ApiResponse.success(convertToDTO(savedProduct), "產品建立成功");
                } catch (Exception e) {
                        return ApiResponse.error(400, "產品建立失敗: " + e.getMessage());
                }
        }

        /**
         * 更新產品
         * 
         * @param id          產品 ID
         * @param formProduct 更新的產品資訊 (JSON 請求體)
         * @return 更新結果的 JSON 回應
         */
        @PutMapping("/{id}")
        public ApiResponse<ProductDTO> updateProduct(
                        @PathVariable Integer id,
                        @RequestBody Product formProduct) {
                try {
                        Product existingProduct = productService.findById(id);
                        if (existingProduct == null) {
                                return ApiResponse.error(404, "產品不存在");
                        }

                        existingProduct.setProductName(formProduct.getProductName());
                        existingProduct.setCategory(formProduct.getCategory());
                        existingProduct.setDescription(formProduct.getDescription());
                        existingProduct.setPrice(formProduct.getPrice());
                        existingProduct.setStock(formProduct.getStock());
                        existingProduct.setImageUrl(formProduct.getImageUrl());
                        existingProduct.setStatus(formProduct.getStatus());

                        Product updatedProduct = productService.save(existingProduct);
                        return ApiResponse.success(convertToDTO(updatedProduct), "產品更新成功");
                } catch (Exception e) {
                        return ApiResponse.error(400, "產品更新失敗: " + e.getMessage());
                }
        }

        /**
         * 刪除產品
         * 
         * @param id 產品 ID
         * @return 刪除結果的 JSON 回應
         */
        @DeleteMapping("/{id}")
        public ApiResponse<String> deleteProduct(@PathVariable Integer id) {
                try {
                        Product product = productService.findById(id);
                        if (product == null) {
                                return ApiResponse.error(404, "產品不存在");
                        }
                        productService.deleteById(id);
                        return ApiResponse.success("產品已刪除", "產品刪除成功");
                } catch (Exception e) {
                        return ApiResponse.error(400, "產品刪除失敗: " + e.getMessage());
                }
        }

        /**
         * 獲取所有類別 (用於前端的選擇列表)
         * 
         * @return 類別列表的 JSON 回應
         */
        @GetMapping("/categories/all")
        public ApiResponse<List<Category>> getAllCategories() {
                List<Category> categories = categoryRepository.findAll();
                return ApiResponse.success(categories, "類別列表載入成功");
        }

        /**
         * 將 Product entity 轉換為 ProductDTO
         * 
         * @param product Product entity
         * @return ProductDTO
         */
        private ProductDTO convertToDTO(Product product) {
                if (product == null) {
                        return null;
                }
                return new ProductDTO(
                                product.getProductId(),
                                product.getProductName(),
                                product.getCategory() != null ? product.getCategory().getCategoryName() : "",
                                product.getCategory() != null ? product.getCategory().getCategoryId() : null,
                                product.getDescription(),
                                product.getPrice(),
                                product.getStock(),
                                product.getImageUrl(),
                                product.getStatus());
        }
}