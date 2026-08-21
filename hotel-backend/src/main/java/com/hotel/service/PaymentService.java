package com.hotel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.model.entity.Payment;
import com.hotel.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment findById(Integer id) {
        return paymentRepository
                .findById(id)
                .orElse(null);
    }

    public List<Payment> findByMemberId(Integer memberId) {
        return paymentRepository.findByMemberId(memberId);
    }

    public List<Payment> findByPaymentStatus(String paymentStatus) {
        return paymentRepository.findByPaymentStatus(paymentStatus);
    }

    public List<Payment> findByPaymentMethod(String paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod);
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void deleteById(Integer id) {
        paymentRepository.deleteById(id);
    }
}
