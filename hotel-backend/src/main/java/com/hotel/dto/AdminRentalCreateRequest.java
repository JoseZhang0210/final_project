package com.hotel.dto;

import java.time.LocalDateTime;

/**
 * 管理員建立場地租借時允許輸入的欄位。
 *
 * 管理員可指定既有會員，
 * rentalId、paymentId、rentalStatus 仍由後端自動處理。
 */
public record AdminRentalCreateRequest(
        Integer memberId,
        Integer venueId,
        String eventName,
        LocalDateTime rentalDate,
        Integer guestCount) {
}