package com.hotel.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @Column(name = "payment_id")
    private Integer id;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "payment_status", nullable = false, length = 50)
    private String paymentStatus;

    @Column(name = "member_id")
    private Integer memberId;

    public Payment(Integer id, String paymentMethod, LocalDateTime paymentTime, Integer totalPrice,
                   String paymentStatus, Integer memberId) {
        this.id = id;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.memberId = memberId;
    }
}
