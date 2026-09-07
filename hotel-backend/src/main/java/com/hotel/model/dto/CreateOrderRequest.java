package com.hotel.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateOrderRequest {

    private Integer memberId;

    private String couponCode;

    private List<CreateOrderItemRequest> items;
}