package com.hotel.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.hotel.model.entity.Coupon;
import com.hotel.model.entity.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDataExchangeDTO {

    private List<Coupon> coupons;
    private List<Payment> payments;
    private List<OrderExportItemDTO> orders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderExportItemDTO {
        private Integer orderId;
        private Integer memberId;
        private LocalDateTime orderDate;
        private Integer originalAmount;
        private Integer discountAmount;
        private Integer finalAmount;
        private Integer couponId;
        private String couponCode;
        private Integer paymentId;
        private String paymentTransactionId;
        private String orderStatus;
        private List<OrderItemDetailDTO> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDetailDTO {
        private Integer productId;
        private Integer quantity;
        private Integer unitPrice;
        private Integer subtotal;
    }
}
