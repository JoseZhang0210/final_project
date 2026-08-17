package com.hotel.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    // 1. 修改關聯型別為 BookingOrder 物件
    // 2. 移除 @JsonIgnore，換成 @JsonIgnoreProperties 避免 JSON 雙向無限遞迴
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties({"bookings"})
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

    /**
     * 專門提供給前端 JSON 欄位（免去前端改結構的麻煩）
     * 當序列化成 JSON 時，會自動產生 "bookingOrderId": 數字
     */
    @JsonProperty("bookingOrderId")
    public Integer getBookingOrderId() {
        return bookingOrder != null ? bookingOrder.getBookingOrderId() : null;
    }
}