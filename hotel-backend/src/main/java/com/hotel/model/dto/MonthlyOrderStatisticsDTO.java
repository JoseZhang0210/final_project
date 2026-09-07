package com.hotel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyOrderStatisticsDTO {

    private Integer year;

    private Integer month;

    private Long orderCount;

    private Long totalRevenue;
}