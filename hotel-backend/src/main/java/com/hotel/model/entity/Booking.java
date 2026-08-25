package com.hotel.model.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "bookingOrder", "room", "roomType" })
@EqualsAndHashCode(exclude = { "bookingOrder", "room", "roomType" })
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    // 外鍵指向 BookingOrder (N:1 關係)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_order_id", nullable = false)
    @JsonIgnoreProperties("bookings")
    private BookingOrder bookingOrder;

    @Column(name = "booking_price", nullable = false)
    private Integer bookingPrice;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "guest_num", nullable = false)
    private Integer guestNum;

    @Column(name = "booking_status", nullable = false, length = 20)
    private String bookingStatus;

    // 外鍵指向 Room (N:1 關係，可為空 null，代表尚未分配具體房間)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = true)
    private Room room;

    // 外鍵指向 RoomType (N:1 關係)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

}