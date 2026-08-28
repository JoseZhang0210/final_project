package com.hotel.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    private Integer orderId;

    private String memberName;
    private String memberPhone;
    private String memberEmail;

    private Integer totalAmount;
    private String status;
    private LocalDateTime orderDate;

    private List<OrderItemDTO> items;

    public OrderDTO() {
    }

    public OrderDTO(
            Integer orderId,
            String memberName,
            String memberPhone,
            String memberEmail,
            Integer totalAmount,
            String status,
            LocalDateTime orderDate,
            List<OrderItemDTO> items) {

        this.orderId = orderId;
        this.memberName = memberName;
        this.memberPhone = memberPhone;
        this.memberEmail = memberEmail;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
        this.items = items;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}