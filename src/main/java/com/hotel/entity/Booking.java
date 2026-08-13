package com.hotel.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BookingOrder bookingOrder;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "check_in_date")
    private Date checkInDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "check_out_date")
    private Date checkOutDate;

    @Column(name = "guest_num")
    private Integer guestNum;

    @Column(name = "booking_status")
    private String bookingStatus;

    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "room_type_id")
    private Integer roomTypeId;

}
