package com.hotel.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "member", "payment", "payments", "bookings" })
@EqualsAndHashCode(exclude = { "member", "payment", "payments", "bookings" })
@Table(name = "booking_order")
public class BookingOrder {

    @Id
    @Column(name = "booking_order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingOrderId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    // 外鍵指向 Member (N:1 關係)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    @JsonIgnore
    private Member member;

    @Column(name = "booking_total_price", nullable = false)
    private Integer bookingTotalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private LocalDateTime createdAt;

    @Column(name = "order_status", nullable = false, length = 20)
    private String orderStatus;

    @Column(name = "payment_id")
    private Integer paymentId;

    @JsonIgnore
    @JoinColumn(name = "payment_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Payment payment;

    // ====================多關聯性Payment,Booking

    @JsonIgnore
    @OneToMany(mappedBy = "bookingOrder")
    private List<Payment> payments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "bookingOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}