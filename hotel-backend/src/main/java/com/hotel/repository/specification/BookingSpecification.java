package com.hotel.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.hotel.model.dto.BookingDTO;
import com.hotel.model.entity.Booking;

import jakarta.persistence.criteria.Predicate;

public class BookingSpecification {
    public static Specification<Booking> findByCriteria(BookingDTO criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria.getBookingId() != null) {
                predicates.add(cb.equal(root.get("bookingId"), criteria.getBookingId()));
            }
            if (criteria.getMemberId() != null) {
                predicates.add(cb.equal(root.get("memberId"), criteria.getMemberId()));
            }
            if (criteria.getRoomTypeId() != null) {
                predicates.add(cb.equal(root.get("roomTypeId"), criteria.getRoomTypeId()));
            }
            if (criteria.getRoomId() != null) {
                predicates.add(cb.equal(root.get("roomId"), criteria.getRoomId()));
            }
            if (criteria.getCheckInDate() != null) {
                predicates.add(cb.equal(root.get("checkInDate"), criteria.getCheckInDate()));
            }
            if (criteria.getCheckOutDate() != null) {
                predicates.add(cb.equal(root.get("checkOutDate"), criteria.getCheckOutDate()));
            }
            if (criteria.getGuestNum() != null) {
                predicates.add(cb.equal(root.get("guestNum"), criteria.getGuestNum()));
            }
            if (criteria.getBookingStatus() != null && !criteria.getBookingStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("bookingStatus"), criteria.getBookingStatus()));
            }
            if (criteria.getBookingPrice() != null) {
                predicates.add(cb.equal(root.get("bookingPrice"), criteria.getBookingPrice()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
