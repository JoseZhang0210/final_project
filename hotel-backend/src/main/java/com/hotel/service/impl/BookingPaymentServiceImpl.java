package com.hotel.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.event.BookingEvents.BookingCreatedEvent;
import com.hotel.model.dto.BookingPaymentDTO;
import com.hotel.model.entity.BookingPayment;
import com.hotel.repository.BookingPaymentRepository;
import com.hotel.service.BookingPaymentService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class BookingPaymentServiceImpl implements BookingPaymentService {

    private final BookingPaymentRepository bookingPaymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BookingPaymentServiceImpl(BookingPaymentRepository bookingPaymentRepository,
            ApplicationEventPublisher eventPublisher) {
        this.bookingPaymentRepository = bookingPaymentRepository;
        this.eventPublisher = eventPublisher;
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

        // 若前端沒有傳入狀態，預設為未付款
        if (payment.getPaymentStatus() == null || payment.getPaymentStatus().isEmpty()) {
            payment.setPaymentStatus("未付款");
        }

        // 如果狀態是「已付款」且沒有付款時間，補上時間
        if ("已付款".equals(payment.getPaymentStatus()) && payment.getPaidAt() == null) {
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

    @Override
    public void ensurePendingPayment(Integer bookingId, Integer amount, String paymentMethod) {
        BookingPayment payment = bookingPaymentRepository.findByBookingId(bookingId).orElse(null);
        String method = (paymentMethod != null && !paymentMethod.isBlank()) ? paymentMethod : "信用卡";

        if (payment == null) {
            payment = new BookingPayment();
            payment.setBookingId(bookingId);
            payment.setAmount(amount);
            payment.setPaymentMethod(method);
            payment.setPaymentStatus("待付款");
            payment.setCreatedAt(LocalDateTime.now());
            payment.setPaidAt(null);
            bookingPaymentRepository.save(payment);
            log.info("建立訂單 ID {} 的待付款紀錄", bookingId);
        } else if (!"已付款".equals(payment.getPaymentStatus())) {
            payment.setPaymentStatus("待付款");
            payment.setPaidAt(null);
            if (amount != null && amount > 0) {
                payment.setAmount(amount);
            }
            bookingPaymentRepository.save(payment);
            log.info("更新訂單 ID {} 的付款紀錄為待付款", bookingId);
        }
    }

    @Override
    public boolean processSuccessfulPayment(Integer bookingId, Integer amount, String paymentMethod,
            String transactionId) {
        BookingPayment payment = bookingPaymentRepository.findByBookingId(bookingId).orElse(null);
        String finalTxnId = (transactionId != null && !transactionId.isBlank())
                ? transactionId
                : "TXN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                        + (System.currentTimeMillis() % 10000);
        String method = (paymentMethod != null && !paymentMethod.isBlank()) ? paymentMethod : "信用卡";

        if (payment == null) {
            payment = new BookingPayment();
            payment.setBookingId(bookingId);
            payment.setAmount(amount);
            payment.setPaymentMethod(method);
            payment.setPaymentStatus("已付款");
            payment.setTransactionId(finalTxnId);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setPaidAt(LocalDateTime.now());
            bookingPaymentRepository.save(payment);

            log.info("訂單 ID {} 首筆付款成功確認，交易序號: {}", bookingId, finalTxnId);
            eventPublisher.publishEvent(new BookingCreatedEvent(bookingId));
            return true;
        }

        // 冪等性檢查：若尚未是「已付款」，才執行狀態變更與發信
        if (!"已付款".equals(payment.getPaymentStatus())) {
            payment.setPaymentStatus("已付款");
            payment.setPaymentMethod(method);
            if (payment.getTransactionId() == null || payment.getTransactionId().isBlank()) {
                payment.setTransactionId(finalTxnId);
            }
            payment.setPaidAt(LocalDateTime.now());
            if (amount != null && amount > 0) {
                payment.setAmount(amount);
            }
            bookingPaymentRepository.save(payment);

            log.info("訂單 ID {} 狀態已更新為已付款，交易序號: {}", bookingId, payment.getTransactionId());
            eventPublisher.publishEvent(new BookingCreatedEvent(bookingId));
            return true;
        }

        log.info("訂單 ID {} 已經為已付款狀態 (冪等防護)，略過重複更新與發信", bookingId);
        return false;
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
        payment.setPaymentId(dto.getPaymentId());
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
