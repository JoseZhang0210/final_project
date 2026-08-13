package com.hotel.dto;

import java.math.BigDecimal;

/**
 * 產品 DTO - 用於 REST API 的前後端資料交換
 */
public class ProductDTO {

    private Integer productId;
    private String productName;
    private String categoryName; // 類別名稱
    private Integer categoryId; // 類別 ID
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private String status;

    public ProductDTO() {
    }

    public ProductDTO(Integer productId, String productName, String categoryName, Integer categoryId,
            String description, BigDecimal price, Integer stock, String imageUrl, String status) {
        this.productId = productId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    // Getter & Setter
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
