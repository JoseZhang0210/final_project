package com.hotel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyProductSalesDTO {

    private Integer productId;

    private String productName;

    private Long quantitySold;

    private Long salesAmount;
}