package com.hotel.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.Payment;
import com.hotel.repository.PaymentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // 1. Read All - 查詢所有付款記錄
    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    // 2. Read by ID - 依 ID 查詢單筆付款
    @Transactional(readOnly = true)
    public Payment findById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的付款記錄"));
    }

    // 3. Create - 新增付款記錄 (可連帶維護與 BookingOrder 的關聯)
    public Payment insert(Payment payment) {
        payment.setPaymentId(null);
        return paymentRepository.save(payment);
    }

    // 4. Update - 修改付款記錄 (利用 Dirty Checking)
    public Payment update(Integer id, Payment formPayment) {
        Payment existingPayment = findById(id);

        if (formPayment.getPaymentMethod() != null) {
            existingPayment.setPaymentMethod(formPayment.getPaymentMethod());
        }
        if (formPayment.getBookingOrder() != null) {
            existingPayment.setBookingOrder(formPayment.getBookingOrder());
        }

        return existingPayment;
    }

    // 5. Delete - 刪除付款記錄
    public void deleteById(Integer id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的付款記錄 ID: " + id + " 不存在");
        }
        paymentRepository.deleteById(id);
    }
}