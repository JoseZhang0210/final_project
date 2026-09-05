package com.hotel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResponse {

    private Integer couponId;

    private String couponCode;

    private String couponName;

    private Integer originalAmount;

    private Integer discountAmount;

    private Integer finalAmount;

}