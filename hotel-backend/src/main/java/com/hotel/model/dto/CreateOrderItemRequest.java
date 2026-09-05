package com.hotel.model.dto;

import lombok.Data;

@Data
public class CreateOrderItemRequest {

    private Integer productId;

    private Integer quantity;
}