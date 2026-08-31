package com.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

        public ProductRestController(ProductService productService) {
                this.productService = productService;
        }

        // =========================================
        // 1. 查詢全部商品
        // GET /api/products
        // =========================================
        @GetMapping
        public ResponseEntity<List<Product>> findAllProducts() {

                List<Product> products = productService.findAllProducts();

                return ResponseEntity.ok(products);
        }

        // 2. 商品名稱模糊查詢
        @GetMapping("/search")
        public ResponseEntity<List<Product>> searchProducts(
                        @RequestParam String keyword) {

                return ResponseEntity.ok(
                                productService.searchByName(keyword));
        }

        // =========================================
        // 2. 查詢單一商品
        // GET /api/products/{id}
        // 例如：GET /api/products/5
        // =========================================
        @GetMapping("/{id}")
        public ResponseEntity<Product> findProductById(
                        @PathVariable Integer id) {

                Product product = productService.findById(id);

                if (product == null) {
                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                return ResponseEntity.ok(product);
        }

        // =========================================
        // 3. 新增商品
        // POST /api/products
        // =========================================
        @PostMapping
        public ResponseEntity<Product> createProduct(
                        @RequestBody Product product) {

                /*
                 * 新增商品時不應由前端指定 ProductID
                 * ProductID 由 SQL Server IDENTITY 自動產生
                 */
                product.setProductId(null);

                /*
                 * 如果前端沒有送 Status
                 * 就預設成 ACTIVE
                 */
                if (product.getStatus() == null ||
                                product.getStatus().isBlank()) {

                        product.setStatus("ACTIVE");
                }

                Product savedProduct = productService.save(product);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(savedProduct);
        }

        // =========================================
        // 4. 修改商品
        // PUT /api/products/{id}
        // 例如：PUT /api/products/5
        // =========================================
        @PutMapping("/{id}")
        public ResponseEntity<Product> updateProduct(
                        @PathVariable Integer id,
                        @RequestBody Product formProduct) {

                Product existingProduct = productService.findById(id);

                /*
                 * 商品不存在
                 */
                if (existingProduct == null) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                /*
                 * 更新商品名稱
                 */
                existingProduct.setProductName(
                                formProduct.getProductName());

                /*
                 * 更新商品分類
                 */
                existingProduct.setCategory(
                                formProduct.getCategory());

                /*
                 * 更新商品描述
                 */
                existingProduct.setDescription(
                                formProduct.getDescription());

                /*
                 * 更新價格
                 */
                existingProduct.setPrice(
                                formProduct.getPrice());

                /*
                 * 更新庫存
                 */
                existingProduct.setStock(
                                formProduct.getStock());

                /*
                 * 更新圖片網址
                 */
                existingProduct.setImageUrl(
                                formProduct.getImageUrl());

                /*
                 * 更新商品狀態ㄋ
                 */
                existingProduct.setStatus(
                                formProduct.getStatus());

                Product updatedProduct = productService.save(
                                existingProduct);

                return ResponseEntity.ok(
                                updatedProduct);
        }

        // =========================================
        // 5. 刪除商品
        // DELETE /api/products/{id}
        // 例如：DELETE /api/products/5
        // =========================================
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(
                        @PathVariable Integer id) {

                Product product = productService.findById(id);

                /*
                 * 商品不存在
                 */
                if (product == null) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                productService.deleteById(id);

                /*
                 * 204 No Content
                 * 代表刪除成功，但是沒有回傳內容
                 */
                return ResponseEntity
                                .noContent()
                                .build();
        }
}