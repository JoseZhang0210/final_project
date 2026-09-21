package com.hotel.model.dto;

import lombok.Data;

@Data
public class AddCartItemRequest {

  private Integer productId;
  private Integer quantity;
}
