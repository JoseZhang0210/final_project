package com.hotel.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon", schema = "dbo")
@Data
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Integer couponId;
    @Column(name = "coupon_code", nullable = false, unique = true, length = 50)
    private String couponCode;
    @Column(name = "coupon_name", nullable = false, length = 100)
    private String couponName;
    @Column(name = "discount_type", nullable = false, length = 20)
    private String discountType;
    @Column(name = "discount_value", nullable = false)
    private Integer discountValue;
    @Column(name = "minimum_amount", nullable = false)
    private Integer minimumAmount = 0;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";
}
