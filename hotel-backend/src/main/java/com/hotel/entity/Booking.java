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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BookingOrder bookingOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Room room; // 允許為 null (若下單當下暫不指定房號)

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "guest_num")
    private Integer guestNum;

    @Column(name = "booking_status")
    private String bookingStatus; // PENDING（預訂中/鎖房）, CONFIRMED（已完成）, CANCELLED（已取消）

    @PrePersist
    protected void onCreate() {
        if (this.bookingStatus == null) {
            this.bookingStatus = "PENDING"; // 新增明細時預設為 PENDING，配合主訂單鎖房
        }
    }
}