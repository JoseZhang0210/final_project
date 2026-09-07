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
@Table(name = "payment", schema = "dbo")
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;
    @Column(name = "member_id")
    private Integer memberId;
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    @Column(name = "transaction_id", length = 100)
    private String transactionId;
    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;
    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "PENDING";
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}