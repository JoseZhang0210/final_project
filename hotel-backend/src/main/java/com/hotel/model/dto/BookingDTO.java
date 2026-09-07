package com.hotel.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookingDTO {
    private Integer bookingId;
    private Integer memberId;
    private Integer roomTypeId;
    private LocalDateTime createdAt;
    private Integer roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestNum;
    private String bookingStatus;
    private Integer bookingPrice;
}
