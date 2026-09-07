package com.hotel.controller;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Payment;
import com.hotel.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

        private final PaymentService paymentService;

        // =====================================================
        // Constructor
        // =====================================================

        public PaymentController(
                        PaymentService paymentService) {

                this.paymentService = paymentService;
        }

        // =====================================================
        // 1. 查詢付款列表
        //
        // GET /api/payments
        //
        // GET /api/payments?memberId=1
        //
        // GET /api/payments?paymentStatus=PAID
        // =====================================================

        @GetMapping
        public ResponseEntity<List<Payment>> findPayments(
                        @RequestParam(required = false) Integer memberId,
                        @RequestParam(required = false) String paymentStatus) {

                // ==============================
                // 會員付款紀錄
                // ==============================

                if (memberId != null) {

                        return ResponseEntity.ok(
                                        paymentService
                                                        .findByMemberId(
                                                                        memberId));
                }

                // ==============================
                // 付款狀態
                // ==============================

                if (paymentStatus != null
                                && !paymentStatus.isBlank()) {

                        String normalizedStatus = paymentStatus
                                        .trim()
                                        .toUpperCase();

                        return ResponseEntity.ok(
                                        paymentService
                                                        .findByPaymentStatus(
                                                                        normalizedStatus));
                }

                // ==============================
                // 全部付款紀錄
                // ==============================

                return ResponseEntity.ok(
                                paymentService
                                                .findAllPayments());
        }

        // =====================================================
        // 2. 查詢單筆付款
        //
        // GET /api/payments/1
        // =====================================================

        @GetMapping("/{id}")
        public ResponseEntity<Payment> findPaymentById(
                        @PathVariable Integer id) {

                Payment payment = paymentService
                                .findById(id);

                if (payment == null) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                return ResponseEntity.ok(
                                payment);
        }

        // =====================================================
        // 3. 依照商品訂單建立 Payment
        //
        // POST
        // /api/payments/order/1?paymentMethod=信用卡
        //
        // 建立後：
        //
        // Payment = PENDING
        // Order = PENDING
        //
        // Order.payment_id
        // 會指向此 Payment
        // =====================================================

        @PostMapping("/order/{orderId}")
        public ResponseEntity<Payment> createPaymentForOrder(
                        @PathVariable Integer orderId,
                        @RequestParam String paymentMethod) {

                // ==============================
                // paymentMethod 檢查
                // ==============================

                if (paymentMethod == null
                                || paymentMethod.isBlank()) {

                        throw new IllegalArgumentException(
                                        "付款方式不能為空");
                }

                Payment payment = paymentService
                                .createPayment(
                                                orderId,
                                                paymentMethod
                                                                .trim());

                return ResponseEntity
                                .status(
                                                HttpStatus.CREATED)
                                .body(
                                                payment);
        }

        // =====================================================
        // 4. 確認付款
        //
        // PUT /api/payments/1/confirm
        //
        // Payment：
        //
        // PENDING
        // ↓
        // PAID
        //
        // 同時更新：
        //
        // payment_time
        // transaction_id
        //
        // 注意：
        //
        // 付款成功後
        // Order 仍維持 PENDING
        //
        // 等櫃台實際交付商品後，
        // 再將 Order 改為 COMPLETED
        // =====================================================

        @PutMapping("/{id}/confirm")
        public ResponseEntity<Payment> confirmPayment(
                        @PathVariable Integer id) {

                Payment payment = paymentService
                                .confirmPayment(
                                                id);

                return ResponseEntity.ok(
                                payment);
        }

        // =====================================================
        // 5. 後台修改 Payment 基本資料
        //
        // PUT /api/payments/1
        //
        // 注意：
        // 這裡不直接修改 payment_status
        // payment_status 使用專用 API
        // =====================================================

        @PutMapping("/{id}")
        public ResponseEntity<Payment> updatePayment(
                        @PathVariable Integer id,
                        @RequestBody Payment formPayment) {

                Payment existingPayment = paymentService
                                .findById(
                                                id);

                if (existingPayment == null) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                // ==============================
                // 付款方式
                // ==============================

                if (formPayment.getPaymentMethod() != null
                                && !formPayment
                                                .getPaymentMethod()
                                                .isBlank()) {

                        existingPayment
                                        .setPaymentMethod(
                                                        formPayment
                                                                        .getPaymentMethod()
                                                                        .trim());
                }

                // ==============================
                // 金流交易編號
                // ==============================

                if (formPayment.getTransactionId() != null
                                && !formPayment
                                                .getTransactionId()
                                                .isBlank()) {

                        existingPayment
                                        .setTransactionId(
                                                        formPayment
                                                                        .getTransactionId()
                                                                        .trim());
                }

                // ==============================
                // 金額
                // ==============================

                if (formPayment.getTotalPrice() != null) {

                        if (formPayment.getTotalPrice() < 0) {

                                throw new IllegalArgumentException(
                                                "付款金額不能小於 0");
                        }

                        existingPayment
                                        .setTotalPrice(
                                                        formPayment
                                                                        .getTotalPrice());
                }

                // ==============================
                // Member
                // ==============================

                if (formPayment.getMemberId() != null) {

                        existingPayment
                                        .setMemberId(
                                                        formPayment
                                                                        .getMemberId());
                }

                Payment updatedPayment = paymentService
                                .save(
                                                existingPayment);

                return ResponseEntity.ok(
                                updatedPayment);
        }

        // =====================================================
        // 6. 後台修改付款狀態
        //
        // PATCH
        // /api/payments/1/status?status=REFUNDED
        //
        // 合法狀態：
        //
        // PENDING
        // PAID
        // FAILED
        // REFUNDED
        //
        // 特別規則：
        //
        // 如果要改成 PAID，
        // 必須使用 confirmPayment()
        //
        // 避免：
        //
        // payment_status = PAID
        //
        // 但是：
        //
        // payment_time = null
        // transaction_id = null
        // =====================================================

        @PatchMapping("/{id}/status")
        public ResponseEntity<?> updatePaymentStatus(
                        @PathVariable Integer id,
                        @RequestParam String status) {

                // ==============================
                // status 檢查
                // ==============================

                if (status == null
                                || status.isBlank()) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(
                                                        "付款狀態不能為空");
                }

                String newStatus = status
                                .trim()
                                .toUpperCase();

                // ==============================
                // 驗證合法狀態
                // ==============================

                if (!"PENDING".equals(newStatus)
                                && !"PAID".equals(newStatus)
                                && !"FAILED".equals(newStatus)
                                && !"REFUNDED".equals(newStatus)) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(
                                                        "不支援的付款狀態："
                                                                        + status);
                }

                Payment existingPayment = paymentService
                                .findById(
                                                id);

                if (existingPayment == null) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                // ==============================
                // 狀態沒有改變
                // ==============================

                if (newStatus.equals(
                                existingPayment
                                                .getPaymentStatus())) {

                        return ResponseEntity.ok(
                                        existingPayment);
                }

                // ==============================
                // 要改成 PAID
                //
                // 必須走確認付款流程
                // ==============================

                if ("PAID".equals(
                                newStatus)) {

                        Payment paidPayment = paymentService
                                        .confirmPayment(
                                                        id);

                        return ResponseEntity.ok(
                                        paidPayment);
                }

                // ==============================
                // 其他狀態
                // ==============================

                existingPayment
                                .setPaymentStatus(
                                                newStatus);

                Payment updatedPayment = paymentService
                                .save(
                                                existingPayment);

                return ResponseEntity.ok(
                                updatedPayment);
        }

        // =====================================================
        // 7. 刪除 Payment
        //
        // DELETE /api/payments/1
        //
        // Payment 是共用資料：
        //
        // 商品訂單
        // 訂房
        // 場地租借
        //
        // 如果已經被其他資料 FK 使用，
        // SQL Server 會阻止刪除。
        // =====================================================

        @DeleteMapping("/{id}")
        public ResponseEntity<?> deletePayment(
                        @PathVariable Integer id) {

                Payment payment = paymentService
                                .findById(
                                                id);

                if (payment == null) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                try {

                        paymentService
                                        .deleteById(
                                                        id);

                        return ResponseEntity
                                        .noContent()
                                        .build();

                } catch (DataIntegrityViolationException e) {

                        return ResponseEntity
                                        .status(
                                                        HttpStatus.CONFLICT)
                                        .body(
                                                        "無法刪除：此付款紀錄已被訂單、訂房或場地租借使用。");
                }
        }
}