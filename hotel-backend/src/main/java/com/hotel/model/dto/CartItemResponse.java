package com.hotel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemResponse {

  private Integer productId;
  private String productName;
  private Integer price;
  private Integer quantity;
  private Integer subtotal;
  private Integer stock;
  private String imageUrl;
  private String status;
  private boolean available;
}
