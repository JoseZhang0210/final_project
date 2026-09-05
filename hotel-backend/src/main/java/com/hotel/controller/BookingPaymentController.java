package com.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.dto.BookingPaymentDTO;
import com.hotel.service.BookingPaymentService;

@RestController
@RequestMapping("/api/booking-payments")
public class BookingPaymentController {

    private final BookingPaymentService bookingPaymentService;

    public BookingPaymentController(BookingPaymentService bookingPaymentService) {
        this.bookingPaymentService = bookingPaymentService;
    }

    @GetMapping
    public ResponseEntity<List<BookingPaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(bookingPaymentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingPaymentDTO> getPaymentById(@PathVariable Integer id) {
        return bookingPaymentService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("找不到付款紀錄 ID: " + id));
    }

    @PostMapping
    public ResponseEntity<BookingPaymentDTO> createPayment(@RequestBody BookingPaymentDTO bookingPaymentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingPaymentService.createPayment(bookingPaymentDTO));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BookingPaymentDTO> updatePaymentStatus(@PathVariable Integer id,
            @RequestBody BookingPaymentDTO bookingPaymentDTO) {
        return ResponseEntity.ok(bookingPaymentService.updatePaymentStatus(id, bookingPaymentDTO.getPaymentStatus()));
    }
}