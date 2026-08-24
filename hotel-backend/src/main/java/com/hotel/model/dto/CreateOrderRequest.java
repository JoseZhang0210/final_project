package com.hotel.model.dto;

import java.util.List;

public class CreateOrderRequest {

    private Integer memberId;

    private List<CreateOrderItemRequest> items;

    public CreateOrderRequest() {
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public List<CreateOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemRequest> items) {
        this.items = items;
    }
}