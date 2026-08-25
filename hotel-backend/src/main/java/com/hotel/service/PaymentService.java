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

    // 1. Read All - 查詢所有付款方式
    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    // 2. Read by ID
    @Transactional(readOnly = true)
    public Payment findById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的付款記錄"));
    }

    // 3. Create - 新增付款方式
    public Payment insert(Payment payment) {
        payment.setPaymentId(null);
        return paymentRepository.save(payment);
    }

    // 4. Update - 修改付款方式 (利用 Dirty Checking 自動更新)
    public Payment update(Integer id, Payment formPayment) {
        Payment existingPayment = findById(id);

        // 僅允許修改付款方式
        if (formPayment.getPaymentMethod() != null) {
            existingPayment.setPaymentMethod(formPayment.getPaymentMethod());
        }

        // 交易結束時 JPA 會自動比對並發送 UPDATE SQL，無須呼叫 save()
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