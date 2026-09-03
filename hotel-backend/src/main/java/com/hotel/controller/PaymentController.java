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

        public PaymentController(PaymentService paymentService) {
                this.paymentService = paymentService;
        }

        // =====================================================
        // 1. 查詢付款列表
        //
        // GET /api/payments
        // GET /api/payments?memberId=1
        // GET /api/payments?paymentStatus=PAID
        // =====================================================
        @GetMapping
        public ResponseEntity<List<Payment>> findPayments(
                        @RequestParam(required = false) Integer memberId,
                        @RequestParam(required = false) String paymentStatus) {

                if (memberId != null) {
                        return ResponseEntity.ok(
                                        paymentService.findByMemberId(memberId));
                }

                if (paymentStatus != null
                                && !paymentStatus.isBlank()) {

                        return ResponseEntity.ok(
                                        paymentService.findByPaymentStatus(
                                                        paymentStatus));
                }

                return ResponseEntity.ok(
                                paymentService.findAllPayments());
        }

        // =====================================================
        // 2. 查詢單筆付款
        //
        // GET /api/payments/1
        // =====================================================
        @GetMapping("/{id}")
        public ResponseEntity<Payment> findPaymentById(
                        @PathVariable Integer id) {

                Payment payment = paymentService.findById(id);

                if (payment == null) {
                        return ResponseEntity
                                        .notFound()
                                        .build();
                }
                return ResponseEntity.ok(payment);
        }

        // =====================================================
        // 3. 依照訂單建立付款
        //
        // POST
        // /api/payments/order/1?paymentMethod=信用卡
        //
        // 建議前端真正使用這支
        // =====================================================
        @PostMapping("/order/{orderId}")
        public ResponseEntity<Payment> createPaymentForOrder(
                        @PathVariable Integer orderId,
                        @RequestParam String paymentMethod) {

                Payment payment = paymentService.createPayment(
                                orderId,
                                paymentMethod);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(payment);
        }

        // =====================================================
        // 4. 模擬確認付款
        //
        // PUT /api/payments/1/confirm
        //
        // PENDING
        // ↓
        // PAID
        //
        // 同時更新：
        // payment_time
        // transaction_id
        // order_status = COMPLETED
        // =====================================================
        @PutMapping("/{id}/confirm")
        public ResponseEntity<Payment> confirmPayment(
                        @PathVariable Integer id) {

                Payment payment = paymentService.confirmPayment(id);

                return ResponseEntity.ok(payment);
        }

        // =====================================================
        // 5. 後台修改 Payment 基本資料
        //
        // PUT /api/payments/1
        // =====================================================
        @PutMapping("/{id}")
        public ResponseEntity<Payment> updatePayment(
                        @PathVariable Integer id,
                        @RequestBody Payment formPayment) {

                Payment existingPayment = paymentService.findById(id);

                if (existingPayment == null) {
                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                // -----------------------------
                // 付款方式
                // -----------------------------
                if (formPayment.getPaymentMethod() != null
                                && !formPayment.getPaymentMethod().isBlank()) {

                        existingPayment.setPaymentMethod(
                                        formPayment.getPaymentMethod());
                }

                // -----------------------------
                // 金流交易編號
                // -----------------------------
                if (formPayment.getTransactionId() != null
                                && !formPayment.getTransactionId().isBlank()) {

                        existingPayment.setTransactionId(
                                        formPayment.getTransactionId());
                }

                // -----------------------------
                // 金額
                // -----------------------------
                if (formPayment.getTotalPrice() != null) {

                        existingPayment.setTotalPrice(
                                        formPayment.getTotalPrice());
                }

                // -----------------------------
                // Member
                // -----------------------------
                if (formPayment.getMemberId() != null) {

                        existingPayment.setMemberId(
                                        formPayment.getMemberId());
                }

                Payment updatedPayment = paymentService.save(
                                existingPayment);

                return ResponseEntity.ok(
                                updatedPayment);
        }

        // =====================================================
        // 6. 後台快速修改付款狀態
        //
        // PATCH
        // /api/payments/1/status?status=REFUNDED
        //
        // 可接受：
        //
        // PENDING
        // PAID
        // FAILED
        // REFUNDED
        // =====================================================
        @PatchMapping("/{id}/status")
        public ResponseEntity<?> updatePaymentStatus(
                        @PathVariable Integer id,
                        @RequestParam String status) {

                Payment existingPayment = paymentService.findById(id);

                if (existingPayment == null) {
                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                String newStatus = status.toUpperCase();

                // -----------------------------
                // 驗證狀態
                // -----------------------------
                if (!newStatus.equals("PENDING")
                                && !newStatus.equals("PAID")
                                && !newStatus.equals("FAILED")
                                && !newStatus.equals("REFUNDED")) {

                        return ResponseEntity
                                        .badRequest()
                                        .body(
                                                        "不支援的付款狀態：" + status);
                }

                existingPayment.setPaymentStatus(
                                newStatus);

                Payment updatedPayment = paymentService.save(
                                existingPayment);

                return ResponseEntity.ok(
                                updatedPayment);
        }

        // =====================================================
        // 7. 刪除 Payment
        //
        // DELETE /api/payments/1
        //
        // 若已被 order / booking_order / rental 使用，
        // DB Foreign Key 會阻止刪除
        // =====================================================
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deletePayment(
                        @PathVariable Integer id) {

                Payment payment = paymentService.findById(id);

                if (payment == null) {
                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                try {

                        paymentService.deleteById(id);

                        return ResponseEntity
                                        .noContent()
                                        .build();

                } catch (DataIntegrityViolationException e) {

                        return ResponseEntity
                                        .status(HttpStatus.CONFLICT)
                                        .body(
                                                        "無法刪除：此付款紀錄已被訂單、訂房或場地租借使用。");
                }
        }
}