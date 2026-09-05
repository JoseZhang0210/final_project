package com.hotel.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Integer orderId;

    private String memberName;

    private String memberPhone;

    private String memberEmail;

    private Integer originalAmount;

    private Integer discountAmount;

    private Integer finalAmount;

    private String orderStatus;

    private String paymentStatus;

    private LocalDateTime orderDate;

    private List<OrderItemDTO> items;
}