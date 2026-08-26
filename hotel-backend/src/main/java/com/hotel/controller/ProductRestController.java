package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Product;
import com.hotel.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

        private final ProductService productService;

        public ProductRestController(
                        ProductService productService) {

                this.productService = productService;
        }

        // =========================================
        // 1. 查詢全部商品
        // GET /api/products
        // =========================================

        @GetMapping
        public ResponseEntity<List<Product>> findAllProducts() {

                List<Product> products = productService
                                .findAllProducts();

                return ResponseEntity
                                .ok(products);
        }

        // =========================================
        // 2. 商品名稱模糊查詢
        // GET /api/products/search
        // =========================================

        @GetMapping("/search")
        public ResponseEntity<List<Product>> searchProducts(
                        @RequestParam String keyword) {

                return ResponseEntity
                                .ok(
                                                productService
                                                                .searchByName(
                                                                                keyword));
        }

        // =========================================
        // 3. 查詢單一商品
        // GET /api/products/{id}
        // =========================================

        @GetMapping("/{id}")
        public ResponseEntity<Product> findProductById(
                        @PathVariable Integer id) {

                Product product = productService
                                .findById(id);

                if (product == null) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                return ResponseEntity
                                .ok(product);
        }

        // =========================================
        // 4. 新增商品
        // POST /api/products
        // =========================================

        @PostMapping
        public ResponseEntity<Product> createProduct(
                        @RequestBody Product product) {

                product.setProductId(null);

                if (product.getStatus() == null ||
                                product.getStatus()
                                                .isBlank()) {

                        product.setStatus(
                                        "ACTIVE");
                }

                Product savedProduct = productService
                                .save(product);

                return ResponseEntity
                                .status(
                                                HttpStatus.CREATED)
                                .body(
                                                savedProduct);
        }

        // =========================================
        // 5. 修改商品
        // PUT /api/products/{id}
        // =========================================

        @PutMapping("/{id}")
        public ResponseEntity<Product> updateProduct(
                        @PathVariable Integer id,
                        @RequestBody Product formProduct) {

                Product existingProduct = productService
                                .findById(id);

                if (existingProduct == null) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                existingProduct.setProductName(
                                formProduct
                                                .getProductName());

                existingProduct.setCategory(
                                formProduct
                                                .getCategory());

                existingProduct.setDescription(
                                formProduct
                                                .getDescription());

                existingProduct.setPrice(
                                formProduct
                                                .getPrice());

                existingProduct.setStock(
                                formProduct
                                                .getStock());

                existingProduct.setImageUrl(
                                formProduct
                                                .getImageUrl());

                existingProduct.setStatus(
                                formProduct
                                                .getStatus());

                Product updatedProduct = productService
                                .save(
                                                existingProduct);

                return ResponseEntity
                                .ok(
                                                updatedProduct);
        }

        // =========================================
        // 6. 快速修改商品上下架狀態
        // PATCH /api/products/{id}/status
        // =========================================

        @PatchMapping("/{id}/status")
        public ResponseEntity<?> updateProductStatus(
                        @PathVariable Integer id,
                        @RequestBody Map<String, String> request) {

                Product existingProduct = productService
                                .findById(id);

                // 商品不存在
                if (existingProduct == null) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                String status = request.get(
                                "status");

                // status 空白
                if (status == null ||
                                status.isBlank()) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(
                                                        Map.of(
                                                                        "message",
                                                                        "商品狀態不可為空"));
                }

                // 允許的商品狀態
                if (!status.equals("ACTIVE") &&
                                !status.equals("INACTIVE") &&
                                !status.equals("OUT_OF_STOCK") &&
                                !status.equals("DISCONTINUED")) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(
                                                        Map.of(
                                                                        "message",
                                                                        "商品狀態不正確"));
                }

                existingProduct.setStatus(
                                status);

                Product updatedProduct = productService
                                .save(
                                                existingProduct);

                return ResponseEntity
                                .ok(
                                                updatedProduct);
        }

        // =========================================
        // 7. 刪除商品
        // DELETE /api/products/{id}
        // =========================================

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(
                        @PathVariable Integer id) {

                Product product = productService
                                .findById(id);

                if (product == null) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                productService
                                .deleteById(id);

                return ResponseEntity
                                .noContent()
                                .build();
        }
}