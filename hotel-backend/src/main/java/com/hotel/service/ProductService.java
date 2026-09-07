package com.hotel.service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.ProductImportResultDTO;
import com.hotel.model.dto.ProductJsonDTO;
import com.hotel.model.entity.Category;
import com.hotel.model.entity.Product;
import com.hotel.repository.CategoryRepository;
import com.hotel.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public List<Product> findAllProducts() {

        List<Product> products = productRepository.findAll();
        products.forEach(Product::synchronizeStockStatus);

        return products;
    }

    @Transactional
    public List<Product> searchByName(String keyword) {

        List<Product> products = productRepository
                .findByProductNameContainingIgnoreCase(keyword);

        products.forEach(Product::synchronizeStockStatus);

        return products;
    }

    @Transactional
    public Product findById(Integer id) {

        Product product = productRepository
                .findById(id)
                .orElse(null);

        if (product != null) {
            product.synchronizeStockStatus();
        }

        return product;
    }

    public Product save(Product product) {

        return productRepository.save(product);
    }

    public void deleteById(Integer id) {

        productRepository.deleteById(id);
    }

    public List<ProductJsonDTO> getProductsForExport() {

        return productRepository.findAll()
                .stream()
                .map(product -> new ProductJsonDTO(
                        product.getProductId(),
                        product.getProductName(),
                        product.getCategory() == null ? null : product.getCategory().getCategoryId(),
                        product.getCategory() == null ? null : product.getCategory().getCategoryName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock(),
                        product.getImageUrl(),
                        product.getStatus()))
                .toList();
    }

    @Transactional
    public ProductImportResultDTO importProducts(List<ProductJsonDTO> importRows) {

        if (importRows == null || importRows.isEmpty()) {
            throw new IllegalArgumentException("匯入檔案沒有商品資料");
        }

        if (importRows.size() > 1000) {
            throw new IllegalArgumentException("一次最多只能匯入 1000 筆商品");
        }

        Set<Integer> categoryIds = new HashSet<>();
        Set<Integer> importedProductIds = new HashSet<>();
        List<String> errors = new ArrayList<>();

        for (int index = 0; index < importRows.size(); index++) {
            ProductJsonDTO row = importRows.get(index);
            int rowNumber = index + 1;

            if (row == null) {
                errors.add("第 " + rowNumber + " 筆：資料不可為空");
                continue;
            }

            if (row.getProductId() != null) {
                if (row.getProductId() <= 0) {
                    errors.add("第 " + rowNumber + " 筆：productId 必須是正整數");
                } else if (!importedProductIds.add(row.getProductId())) {
                    errors.add("第 " + rowNumber + " 筆：productId 重複");
                }
            }

            if (row.getProductName() == null || row.getProductName().isBlank()) {
                errors.add("第 " + rowNumber + " 筆：productName 不可為空");
            }

            if (row.getCategoryId() == null || row.getCategoryId() <= 0) {
                errors.add("第 " + rowNumber + " 筆：categoryId 不正確");
            } else {
                categoryIds.add(row.getCategoryId());
            }

            if (row.getPrice() == null || row.getPrice() < 0) {
                errors.add("第 " + rowNumber + " 筆：price 必須是 0 以上的整數");
            }

            if (row.getStock() == null || row.getStock() < 0) {
                errors.add("第 " + rowNumber + " 筆：stock 必須是 0 以上的整數");
            }

            String status = normalizeStatus(row.getStatus());
            if (!isValidStatus(status)) {
                errors.add("第 " + rowNumber + " 筆：status 不正確");
            }
        }

        Map<Integer, Category> categoryMap = new HashMap<>();
        categoryRepository.findAllById(categoryIds)
                .forEach(category -> categoryMap.put(category.getCategoryId(), category));

        for (int index = 0; index < importRows.size(); index++) {
            ProductJsonDTO row = importRows.get(index);
            if (row != null && row.getCategoryId() != null && row.getCategoryId() > 0
                    && !categoryMap.containsKey(row.getCategoryId())) {
                errors.add("第 " + (index + 1) + " 筆：找不到 categoryId " + row.getCategoryId());
            }
        }

        if (!errors.isEmpty()) {
            return new ProductImportResultDTO(importRows.size(), 0, 0, errors.size(), errors);
        }

        Map<Integer, Product> existingProductMap = new HashMap<>();
        productRepository.findAllById(importedProductIds)
                .forEach(product -> existingProductMap.put(product.getProductId(), product));

        int createdCount = 0;
        int updatedCount = 0;
        List<Product> productsToSave = new ArrayList<>();

        for (ProductJsonDTO row : importRows) {
            Product product = row.getProductId() == null
                    ? null
                    : existingProductMap.get(row.getProductId());

            if (product == null) {
                product = new Product();
                product.setProductId(null);
                createdCount++;
            } else {
                updatedCount++;
            }

            product.setProductName(row.getProductName().trim());
            product.setCategory(categoryMap.get(row.getCategoryId()));
            product.setDescription(trimToNull(row.getDescription()));
            product.setPrice(row.getPrice());
            product.setStock(row.getStock());
            product.setImageUrl(trimToNull(row.getImageUrl()));
            product.setStatus(normalizeStatus(row.getStatus()));
            productsToSave.add(product);
        }

        productRepository.saveAll(productsToSave);

        return new ProductImportResultDTO(
                importRows.size(),
                createdCount,
                updatedCount,
                0,
                List.of());
    }

    private String normalizeStatus(String status) {
        return status == null || status.isBlank()
                ? "ACTIVE"
                : status.trim().toUpperCase();
    }

    private boolean isValidStatus(String status) {
        return Set.of("ACTIVE", "INACTIVE", "OUT_OF_STOCK", "DISCONTINUED")
                .contains(status);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

}
