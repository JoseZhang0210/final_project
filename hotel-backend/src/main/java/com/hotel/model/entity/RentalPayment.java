package com.hotel.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rental_payment", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "merchant_trade_no", length = 50)
    private String merchantTradeNo;

    @Column(name = "ecpay_trade_no", length = 50)
    private String ecpayTradeNo;
}

