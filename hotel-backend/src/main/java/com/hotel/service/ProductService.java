package com.hotel.service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
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
import com.hotel.repository.ProductReviewRepository;

import jakarta.persistence.EntityManager;

@Service
public class ProductService {

    private static final List<DemoProduct> DEMO_PRODUCTS = List.of(
            new DemoProduct(
                    "星澄舒眠羽絨枕",
                    "飯店寢具",
                    "支撐頸部曲線的飯店規格羽絨枕，將旅途中的舒適帶回家。",
                    1680,
                    25,
                    "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?auto=format&fit=crop&w=900&q=80",
                    "ACTIVE"),
            new DemoProduct(
                    "雲感純棉浴袍",
                    "飯店寢具",
                    "柔軟親膚的純棉浴袍，提供舒適且放鬆的居家體驗。",
                    2280,
                    12,
                    "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=900&q=80",
                    "ACTIVE"),
            new DemoProduct(
                    "星澄經典馬克杯",
                    "旅宿紀念",
                    "以星澄品牌色打造的日常馬克杯，適合咖啡與茶飲。",
                    580,
                    30,
                    "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?auto=format&fit=crop&w=900&q=80",
                    "ACTIVE"),
            new DemoProduct(
                    "城市夜光行李吊牌",
                    "旅宿紀念",
                    "簡約耐用的行李識別吊牌，陪伴每一趟安心旅程。",
                    420,
                    4,
                    "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80",
                    "ACTIVE"),
            new DemoProduct(
                    "靜謐森林擴香",
                    "香氛沐浴",
                    "融合木質與草本氣息，重現星澄客房沉靜放鬆的香氣。",
                    1280,
                    18,
                    "https://images.unsplash.com/photo-1603006905003-be475563bc59?auto=format&fit=crop&w=900&q=80",
                    "ACTIVE"),
            new DemoProduct(
                    "晨曦沐浴禮盒",
                    "香氛沐浴",
                    "包含沐浴露、洗髮露與潤膚乳的旅行沐浴組合。",
                    980,
                    0,
                    "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=900&q=80",
                    "OUT_OF_STOCK"),
            new DemoProduct(
                    "主廚手工餅乾",
                    "餐飲選品",
                    "飯店主廚每日手工烘焙，奶油香氣酥脆而不甜膩。",
                    360,
                    40,
                    "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?auto=format&fit=crop&w=900&q=80",
                    "ACTIVE"),
            new DemoProduct(
                    "星澄精品咖啡豆",
                    "餐飲選品",
                    "中度烘焙精品咖啡豆，帶有堅果、焦糖與淡雅果香。",
                    680,
                    20,
                    "https://images.unsplash.com/photo-1447933601403-0c6688de566e?auto=format&fit=crop&w=900&q=80",
                    "INACTIVE"));

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReviewRepository productReviewRepository;
    private final EntityManager entityManager;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductReviewRepository productReviewRepository,
            EntityManager entityManager) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productReviewRepository = productReviewRepository;
        this.entityManager = entityManager;
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

    @Transactional
    public ProductDeleteResult deleteById(Integer id) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return null;
        }

        if (hasOrderItems(id)) {
            product.setStatus("DISCONTINUED");
            productRepository.save(product);

            return new ProductDeleteResult(
                    false,
                    true,
                    "此商品已有訂單紀錄，為保留交易明細，已改為停售並從前台隱藏。");
        }

        productReviewRepository.deleteByProductId(id);
        productRepository.delete(product);

        return new ProductDeleteResult(
                true,
                false,
                "商品刪除成功。");
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

    @Transactional
    public ProductDemoSeedResult seedDemoProducts() {

        Set<String> existingProductNames = new HashSet<>();
        productRepository.findAll().forEach(product -> {
            if (product.getProductName() != null) {
                existingProductNames.add(product.getProductName().trim().toLowerCase(Locale.ROOT));
            }
        });

        Map<String, Category> categoryMap = new HashMap<>();
        int createdCategoryCount = 0;
        int skippedCount = 0;
        List<Product> productsToCreate = new ArrayList<>();

        for (DemoProduct demoProduct : DEMO_PRODUCTS) {
            String productNameKey = demoProduct.productName().toLowerCase(Locale.ROOT);

            if (existingProductNames.contains(productNameKey)) {
                skippedCount++;
                continue;
            }

            Category category = categoryMap.get(demoProduct.categoryName());
            if (category == null) {
                category = categoryRepository
                        .findByCategoryNameIgnoreCase(demoProduct.categoryName())
                        .orElse(null);

                if (category == null) {
                    category = new Category();
                    category.setCategoryName(demoProduct.categoryName());
                    category = categoryRepository.save(category);
                    createdCategoryCount++;
                }

                categoryMap.put(demoProduct.categoryName(), category);
            }

            Product product = new Product();
            product.setProductName(demoProduct.productName());
            product.setCategory(category);
            product.setDescription(demoProduct.description());
            product.setPrice(demoProduct.price());
            product.setStock(demoProduct.stock());
            product.setImageUrl(demoProduct.imageUrl());
            product.setStatus(demoProduct.status());
            productsToCreate.add(product);
            existingProductNames.add(productNameKey);
        }

        productRepository.saveAll(productsToCreate);

        int createdCount = productsToCreate.size();
        String message = createdCount == 0
                ? "展示商品已經存在，沒有新增重複資料。"
                : "展示資料建立完成：新增 " + createdCount + " 筆商品、"
                        + createdCategoryCount + " 個分類，略過 " + skippedCount + " 筆既有商品。";

        return new ProductDemoSeedResult(
                DEMO_PRODUCTS.size(),
                createdCount,
                skippedCount,
                createdCategoryCount,
                message);
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

    private boolean hasOrderItems(Integer productId) {
        Number count = (Number) entityManager
                .createNativeQuery("SELECT COUNT(*) FROM order_item WHERE product_id = :productId")
                .setParameter("productId", productId)
                .getSingleResult();

        return count.longValue() > 0;
    }

    public record ProductDeleteResult(
            boolean deleted,
            boolean archived,
            String message) {
    }

    public record ProductDemoSeedResult(
            int totalCount,
            int createdCount,
            int skippedCount,
            int createdCategoryCount,
            String message) {
    }

    private record DemoProduct(
            String productName,
            String categoryName,
            String description,
            int price,
            int stock,
            String imageUrl,
            String status) {
    }

}
