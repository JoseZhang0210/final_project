package com.hotel.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.CustomerOrder;
import com.hotel.model.entity.Payment;
import com.hotel.repository.CustomerOrderRepository;
import com.hotel.repository.PaymentRepository;

@Service
public class PaymentService {

        private final PaymentRepository paymentRepository;

        private final CustomerOrderRepository customerOrderRepository;

        public PaymentService(
                        PaymentRepository paymentRepository,
                        CustomerOrderRepository customerOrderRepository) {

                this.paymentRepository = paymentRepository;

                this.customerOrderRepository = customerOrderRepository;
        }

        // =========================================
        // 1. 查詢全部付款
        // =========================================

        public List<Payment> findAllPayments() {

                return paymentRepository
                                .findAll();
        }

        // =========================================
        // 2. 依 memberId 查詢
        // =========================================

        public List<Payment> findByMemberId(
                        Integer memberId) {

                return paymentRepository
                                .findByMemberId(
                                                memberId);
        }

        // =========================================
        // 3. 依付款狀態查詢
        // =========================================

        public List<Payment> findByPaymentStatus(
                        String paymentStatus) {

                return paymentRepository
                                .findByPaymentStatus(
                                                paymentStatus);
        }

        // =========================================
        // 4. 依 paymentId 查詢
        // =========================================

        public Payment findById(
                        Integer id) {

                return paymentRepository
                                .findById(id)
                                .orElse(null);
        }

        // =========================================
        // 5. 儲存 / 更新 Payment
        // =========================================

        public Payment save(
                        Payment payment) {

                return paymentRepository
                                .save(payment);
        }

        // =========================================
        // 6. 刪除 Payment
        // =========================================

        public void deleteById(
                        Integer id) {

                paymentRepository
                                .deleteById(id);
        }

        // =========================================
        // 7. 依訂單建立付款紀錄
        //
        // order = PENDING
        // payment = PENDING
        // =========================================

        @Transactional
        public Payment createPayment(
                        Integer orderId,
                        String paymentMethod) {

                // =============================
                // 找訂單
                // =============================

                CustomerOrder order = customerOrderRepository
                                .findById(orderId)
                                .orElseThrow(
                                                () -> new RuntimeException(
                                                                "找不到訂單"));

                // =============================
                // 已取消訂單不能付款
                // =============================

                if ("CANCELLED".equals(
                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "已取消訂單不能付款");
                }

                // =============================
                // 已完成訂單不能重新付款
                // =============================

                if ("COMPLETED".equals(
                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "已完成訂單不能重新付款");
                }

                // =============================
                // 防止重複建立 Payment
                // =============================

                if (order.getPaymentId() != null) {

                        Payment existingPayment = paymentRepository
                                        .findById(
                                                        order.getPaymentId())
                                        .orElse(null);

                        if (existingPayment != null) {

                                return existingPayment;
                        }
                }

                // =============================
                // 建立 Payment
                // =============================

                Payment payment = new Payment();

                payment.setMemberId(
                                order.getMemberId());

                payment.setPaymentMethod(
                                paymentMethod);

                payment.setTotalPrice(
                                order.getFinalAmount());

                payment.setPaymentStatus(
                                "PENDING");

                payment.setCreatedAt(
                                LocalDateTime.now());

                // 尚未成功付款
                payment.setPaymentTime(
                                null);

                payment.setTransactionId(
                                null);

                Payment savedPayment = paymentRepository
                                .save(payment);

                // =============================
                // 回填 payment_id 到 order
                // =============================

                order.setPaymentId(
                                savedPayment.getPaymentId());

                // 注意：
                // 建立付款資料時
                // orderStatus 仍然保持 PENDING

                customerOrderRepository
                                .save(order);

                return savedPayment;
        }

        // =========================================
        // 8. 模擬付款成功
        //
        // Payment:
        // PENDING → PAID
        //
        // Order:
        // 仍然維持 PENDING
        //
        // 等管理員交付商品後
        // 才改 COMPLETED
        // =========================================

        @Transactional
        public Payment confirmPayment(
                        Integer paymentId) {

                Payment payment = paymentRepository
                                .findById(paymentId)
                                .orElseThrow(
                                                () -> new RuntimeException(
                                                                "找不到付款資料"));

                // =============================
                // 已付款就直接回傳
                // 防止重複付款
                // =============================

                if ("PAID".equals(
                                payment.getPaymentStatus())) {

                        return payment;
                }

                // =============================
                // 付款成功
                // =============================

                payment.setPaymentStatus(
                                "PAID");

                payment.setPaymentTime(
                                LocalDateTime.now());

                payment.setTransactionId(
                                "TEST-PAY-" +
                                                paymentId +
                                                "-" +
                                                System.currentTimeMillis());

                Payment savedPayment = paymentRepository
                                .save(payment);

                // =============================
                // 確認這筆 Payment 有對應訂單
                // =============================

                CustomerOrder order = customerOrderRepository
                                .findByPaymentId(
                                                paymentId)
                                .orElseThrow(
                                                () -> new RuntimeException(
                                                                "找不到對應訂單"));

                // =========================================
                // 非常重要：
                //
                // 付款成功不代表商品已交付
                //
                // 所以這裡不要：
                //
                // order.setOrderStatus("COMPLETED");
                //
                // 訂單繼續保持：
                //
                // PENDING
                //
                // 等管理員交付商品後
                // 再透過 OrderService
                // 改成 COMPLETED
                // =========================================

                return savedPayment;
        }
}