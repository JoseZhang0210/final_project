package com.hotel.model.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookingPaymentDTO {
    private Integer paymentId;
    private Integer bookingId;
    private Integer amount;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
