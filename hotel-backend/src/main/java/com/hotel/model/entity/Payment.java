package com.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "payment_method")
    private String paymentMethod;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "booking_order_id") // 修正：指向 BookingOrder 在 DB
    // 中的外鍵欄位（請確認資料庫實際欄位名）
    // @JsonIgnoreProperties({ "payments", "hibernateLazyInitializer", "handler" })
    // private BookingOrder bookingOrder;
}