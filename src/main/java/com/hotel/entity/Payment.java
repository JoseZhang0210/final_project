package com.hotel.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @Column(name = "payment_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "payment_status", nullable = false, length = 50)
    private String paymentStatus;

    public Payment(Integer id, Member member, String paymentMethod, LocalDateTime paymentTime,
            Integer totalPrice, String paymentStatus) {
        this.id = id;
        this.member = member;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
    }
}
