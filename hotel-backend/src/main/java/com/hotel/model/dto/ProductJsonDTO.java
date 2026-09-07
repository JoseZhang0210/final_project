package com.hotel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductJsonDTO {

    private Integer productId;

    private String productName;

    private Integer categoryId;

    private String categoryName;

    private String description;

    private Integer price;

    private Integer stock;

    private String imageUrl;

    private String status;
}
