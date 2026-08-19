package com.hotel.dto.roombooking;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Integer bookingId;
    private Integer bookingOrderId;
    private Integer roomTypeId;
    private Integer roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestNum;
    private String bookingStatus;

}