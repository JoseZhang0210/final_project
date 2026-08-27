package com.hotel.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemId implements Serializable {

    private Integer orderId;
    private Integer productId;

    public OrderItemId() {
    }

    public OrderItemId(
            Integer orderId,
            Integer productId) {

        this.orderId = orderId;
        this.productId = productId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof OrderItemId)) {
            return false;
        }

        OrderItemId other = (OrderItemId) obj;

        return Objects.equals(
                orderId,
                other.orderId)
                &&
                Objects.equals(
                        productId,
                        other.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                orderId,
                productId);
    }
}