package com.hotel.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "booking_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingOrder {

    @Id
    @Column(name = "booking_order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingOrderId;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "booking_total_price")
    private Integer bookingTotalPrice;

    @Column(name = "order_status")
    private String orderStatus; // PENDING, PAID, EXPIRED, CANCELLED

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime createdAt;

    @Column(name = "payment_id")
    private Integer paymentId;

}