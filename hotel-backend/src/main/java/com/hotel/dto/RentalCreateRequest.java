package com.hotel.dto;

import java.time.LocalDateTime;

/**
 * 會員建立場地租借時允許輸入的欄位。
 *
 * rentalId、memberId、paymentId、rentalStatus
 * 一律由後端處理，不接受前端自行指定。
 */
public record RentalCreateRequest(
        Integer venueId,
        String eventName,
        LocalDateTime rentalDate,
        Integer guestCount) {
}