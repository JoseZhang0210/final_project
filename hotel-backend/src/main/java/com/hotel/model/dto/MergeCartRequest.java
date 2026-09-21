package com.hotel.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class MergeCartRequest {

  private List<AddCartItemRequest> items;
}
