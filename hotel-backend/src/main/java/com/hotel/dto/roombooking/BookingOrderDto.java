package com.hotel.dto.roombooking;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingOrderDto {

    private Integer bookingOrderId;
    private Integer memberId;
    private Integer bookingTotalPrice;
    private String orderStatus;
    private LocalDateTime createdAt;
    private Integer paymentId;
    private List<BookingDto> bookings;

}