package com.hotel.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.BookingPaymentDTO;
import com.hotel.model.entity.BookingPayment;
import com.hotel.repository.BookingPaymentRepository;
import com.hotel.service.BookingPaymentService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class BookingPaymentServiceImpl implements BookingPaymentService {

    private final BookingPaymentRepository bookingPaymentRepository;

    public BookingPaymentServiceImpl(BookingPaymentRepository bookingPaymentRepository) {
        this.bookingPaymentRepository = bookingPaymentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingPaymentDTO> findAll() {
        return bookingPaymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookingPaymentDTO> findById(Integer paymentId) {
        return bookingPaymentRepository.findById(paymentId).map(this::convertToDTO);
    }

    @Override
    public BookingPaymentDTO createPayment(BookingPaymentDTO bookingPaymentDTO) {
        BookingPayment payment = convertToEntity(bookingPaymentDTO);
        payment.setCreatedAt(LocalDateTime.now());

        String method = payment.getPaymentMethod();
        boolean isCash = "現金".equals(method);

        if (isCash) {
            // 現金付款：
            // 1. 不可有交易序號（現金沒有經過金流閘道）
            // 2. 預設狀態為「未付款」，等顧客到現場結帳後手動改狀態
            payment.setTransactionId(null);
            if (payment.getPaymentStatus() == null) {
                payment.setPaymentStatus("未付款");
            }
        } else if (method != null) {
            // 非現金（信用卡、LINE PAY、Apple PAY、銀行轉帳）：
            // 代表已透過金流閘道完成付款，自動設為「已付款」並記錄付款時間
            // transactionId 由前端從金流回呼 (callback) 傳入，或日後由綠界 webhook 寫入
            payment.setPaymentStatus("已付款");
            payment.setPaidAt(LocalDateTime.now());
        }

        BookingPayment saved = bookingPaymentRepository.save(payment);
        return convertToDTO(saved);
    }

    @Override
    public BookingPaymentDTO updatePaymentStatus(Integer id, String status) {
        BookingPayment payment = bookingPaymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到付款紀錄 ID: " + id));
        payment.setPaymentStatus(status);
        if ("PAID".equalsIgnoreCase(status) || "已付款".equals(status)) {
            payment.setPaidAt(LocalDateTime.now());
        }
        return convertToDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingPaymentDTO findByBookingId(Integer bookingId) {
        return bookingPaymentRepository.findByBookingId(bookingId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public BookingPaymentDTO update(Integer id, BookingPaymentDTO dto) {
        BookingPayment payment = bookingPaymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到付款紀錄 ID: " + id));
        
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentStatus(dto.getPaymentStatus());
        payment.setTransactionId(dto.getTransactionId());
        payment.setPaidAt(dto.getPaidAt());
        
        return convertToDTO(payment);
    }

    private BookingPaymentDTO convertToDTO(BookingPayment payment) {
        BookingPaymentDTO dto = new BookingPaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setBookingId(payment.getBookingId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }

    private BookingPayment convertToEntity(BookingPaymentDTO dto) {
        BookingPayment payment = new BookingPayment();
        payment.setBookingId(dto.getBookingId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentStatus(dto.getPaymentStatus());
        payment.setTransactionId(dto.getTransactionId());
        payment.setCreatedAt(dto.getCreatedAt());
        payment.setPaidAt(dto.getPaidAt());
        return payment;
    }
}
