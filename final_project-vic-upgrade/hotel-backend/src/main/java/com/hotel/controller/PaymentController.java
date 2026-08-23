package com.hotel.controller;

import java.time.LocalDateTime;
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

    // =========================================
    // 1. 查詢付款列表（支援依 memberId / paymentStatus / paymentMethod 篩選）
    // GET /api/payments
    // 例如：GET /api/payments
    // GET /api/payments?memberId=1
    // GET /api/payments?paymentStatus=已付款
    // GET /api/payments?paymentMethod=信用卡
    // =========================================
    @GetMapping
    public ResponseEntity<List<Payment>> findPayments(
            @RequestParam(required = false) Integer memberId,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String paymentMethod) {

        if (memberId != null) {
            return ResponseEntity.ok(paymentService.findByMemberId(memberId));
        }

        if (paymentStatus != null && !paymentStatus.isBlank()) {
            return ResponseEntity.ok(paymentService.findByPaymentStatus(paymentStatus));
        }

        if (paymentMethod != null && !paymentMethod.isBlank()) {
            return ResponseEntity.ok(paymentService.findByPaymentMethod(paymentMethod));
        }

        List<Payment> payments = paymentService.findAllPayments();
        return ResponseEntity.ok(payments);
    }

    // =========================================
    // 2. 查詢單一付款詳細資料
    // GET /api/payments/{id}
    // 例如：GET /api/payments/1
    // =========================================
    @GetMapping("/{id}")
    public ResponseEntity<Payment> findPaymentById(@PathVariable Integer id) {

        Payment payment = paymentService.findById(id);

        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(payment);
    }

    // =========================================
    // 3. 新增付款記錄
    // POST /api/payments
    // =========================================
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {

        /*
         * 新增付款時不應由前端指定 PaymentID
         * PaymentID 由 SQL Server IDENTITY 自動產生
         */
        payment.setPaymentId(null);

        /*
         * 若前端未帶入付款時間，預設使用當前系統時間
         */
        if (payment.getPaymentTime() == null) {
            payment.setPaymentTime(LocalDateTime.now());
        }

        /*
         * 若前端未送 paymentStatus，預設成「待付款」
         */
        if (payment.getPaymentStatus() == null || payment.getPaymentStatus().isBlank()) {
            payment.setPaymentStatus("待付款");
        }

        Payment savedPayment = paymentService.save(payment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedPayment);
    }

    // =========================================
    // 4. 修改付款資料
    // PUT /api/payments/{id}
    // 例如：PUT /api/payments/1
    // =========================================
    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(
            @PathVariable Integer id,
            @RequestBody Payment formPayment) {

        Payment existingPayment = paymentService.findById(id);

        /*
         * 付款記錄不存在
         */
        if (existingPayment == null) {
            return ResponseEntity.notFound().build();
        }

        /*
         * 更新付款方式
         */
        existingPayment.setPaymentMethod(formPayment.getPaymentMethod());

        /*
         * 更新付款時間
         */
        if (formPayment.getPaymentTime() != null) {
            existingPayment.setPaymentTime(formPayment.getPaymentTime());
        }

        /*
         * 更新金額
         */
        if (formPayment.getTotalPrice() != null) {
            existingPayment.setTotalPrice(formPayment.getTotalPrice());
        }

        /*
         * 更新付款狀態
         */
        if (formPayment.getPaymentStatus() != null && !formPayment.getPaymentStatus().isBlank()) {
            existingPayment.setPaymentStatus(formPayment.getPaymentStatus());
        }

        /*
         * 更新會員 ID
         */
        existingPayment.setMemberId(formPayment.getMemberId());

        Payment updatedPayment = paymentService.save(existingPayment);

        return ResponseEntity.ok(updatedPayment);
    }

    // =========================================
    // 5. 快速更新付款狀態（如：退款、確認付款）
    // PATCH /api/payments/{id}/status
    // 例如：PATCH /api/payments/1/status?status=已退款
    // =========================================
    @PatchMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestParam String status) {

        Payment existingPayment = paymentService.findById(id);

        /*
         * 付款記錄不存在
         */
        if (existingPayment == null) {
            return ResponseEntity.notFound().build();
        }

        existingPayment.setPaymentStatus(status);

        Payment updatedPayment = paymentService.save(existingPayment);

        return ResponseEntity.ok(updatedPayment);
    }

    // =========================================
    // 6. 刪除付款記錄
    // DELETE /api/payments/{id}
    // 例如：DELETE /api/payments/1
    // =========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Integer id) {

        Payment payment = paymentService.findById(id);

        /*
         * 付款記錄不存在
         */
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            paymentService.deleteById(id);

            /*
             * 204 No Content
             * 代表刪除成功，無回傳內容
             */
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            /*
             * 若該付款記錄已有關聯訂單（booking_order, order, rental），回傳 409 Conflict
             */
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("無法刪除：該付款記錄已被其他訂單關聯。");
        }
    }
}
