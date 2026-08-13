package com.hotel.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private Integer memberid;

    @Column(name = "booking_total_price")
    private Integer bookingTotalPrice;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "payment_id")
    private Integer paymentId;

    // 一對多雙向關聯：指向 Booking 中的 bookingOrder 屬性
    @OneToMany(mappedBy = "bookingOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Booking> bookings = new ArrayList<>();

}