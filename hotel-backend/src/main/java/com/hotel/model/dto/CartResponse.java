package com.hotel.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponse {

  private List<CartItemResponse> items;
  private Integer totalQuantity;
  private Integer totalAmount;
}
