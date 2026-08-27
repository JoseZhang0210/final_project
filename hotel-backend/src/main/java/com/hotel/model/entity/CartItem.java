package com.hotel.model.entity;

import java.math.BigDecimal;

public class CartItem {

    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(
            Integer productId,
            String productName,
            BigDecimal price,
            Integer quantity) {

        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {

        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }

        return price.multiply(
                BigDecimal.valueOf(quantity));
    }

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}