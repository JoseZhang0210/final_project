package com.hotel.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "booking_order_id")
    private Integer bookingOrderId;

    @Column(name = "room_type_id")
    private String roomTypeId;

    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "guest_num")
    private Integer guestNum;

    @Column(name = "booking_status")
    private String bookingStatus;

}