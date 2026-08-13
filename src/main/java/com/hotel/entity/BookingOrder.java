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
    private Integer memberId;

    @Column(name = "booking_total_price")
    private Integer bookingTotalPrice;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "payment_id")
    private Integer paymentId;

    @OneToMany(mappedBy = "bookingOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Booking> bookings = new ArrayList<>();

    // 在 BookingOrder.java 類別內新增這兩個 helper 方法
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setBookingOrder(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setBookingOrder(null);
    }
}
