package com.hotel.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.dto.CouponValidationResponse;
import com.hotel.model.entity.Coupon;
import com.hotel.service.CouponService;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(
            CouponService couponService) {

        this.couponService = couponService;
    }

    // =====================================================
    // 查詢全部優惠券
    //
    // GET /api/coupons
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Coupon>> findAll() {

        return ResponseEntity.ok(
                couponService.findAll());
    }

    // =====================================================
    // 查詢單張優惠券
    //
    // GET /api/coupons/1
    // =====================================================

    @GetMapping("/{couponId}")
    public ResponseEntity<Coupon> findById(
            @PathVariable Integer couponId) {

        return ResponseEntity.ok(
                couponService.findById(
                        couponId));
    }

    // =====================================================
    // 驗證優惠券
    //
    // GET
    // /api/coupons/validate
    // ?code=ANNIVERSARY90&amount=1000
    // =====================================================

    @GetMapping("/validate")
    public ResponseEntity<CouponValidationResponse> validateCoupon(
            @RequestParam String code,
            @RequestParam Integer amount) {

        return ResponseEntity.ok(
                couponService.validateCoupon(
                        code,
                        amount));
    }

    // =====================================================
    // 新增優惠券
    //
    // POST /api/coupons
    // =====================================================

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(
            @RequestBody Coupon coupon) {

        return ResponseEntity.ok(
                couponService.createCoupon(
                        coupon));
    }

    // =====================================================
    // 修改優惠券
    //
    // PUT /api/coupons/1
    // =====================================================

    @PutMapping("/{couponId}")
    public ResponseEntity<Coupon> updateCoupon(
            @PathVariable Integer couponId,
            @RequestBody Coupon coupon) {

        return ResponseEntity.ok(
                couponService.updateCoupon(
                        couponId,
                        coupon));
    }

    // =====================================================
    // 啟用 / 停用優惠券
    //
    // PUT /api/coupons/1/status?status=INACTIVE
    // =====================================================

    @PutMapping("/{couponId}/status")
    public ResponseEntity<Coupon> updateStatus(
            @PathVariable Integer couponId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                couponService.updateStatus(
                        couponId,
                        status));
    }
}