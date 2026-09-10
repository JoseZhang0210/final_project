package com.hotel.dto; // 占用回應獨立於私人租借實體。
import java.time.LocalDate; // 整日租借只需要日期。
public record RentalAvailability(Integer venueId, LocalDate date, boolean occupied) {} // 僅公開場地、日期及占用旗標。
