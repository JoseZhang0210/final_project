package com.hotel.model.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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

    @Column(name = "booking_order_id", nullable = false)
    private Integer bookingOrderId;

    // 外鍵指向 BookingOrder (N:1 關係)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_order_id", insertable = false, updatable = false)
    @JsonIgnore
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
    // ================多關聯
    @Column(name = "room_id")
    private Integer roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    @JsonIgnore
    private Room room;

    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    @JsonIgnore
    private RoomType roomType;

}