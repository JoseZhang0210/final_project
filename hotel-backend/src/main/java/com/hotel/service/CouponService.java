package com.hotel.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.CouponValidationResponse;
import com.hotel.model.entity.Coupon;
import com.hotel.repository.CouponRepository;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(
            CouponRepository couponRepository) {

        this.couponRepository = couponRepository;
    }

    // =====================================================
    // 查詢全部優惠券
    // =====================================================

    public List<Coupon> findAll() {

        return couponRepository.findAll();
    }

    // =====================================================
    // 依 ID 查詢
    // =====================================================

    public Coupon findById(
            Integer couponId) {

        return couponRepository
                .findById(couponId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "找不到優惠券"));
    }

    // =====================================================
    // 新增優惠券
    // =====================================================

    @Transactional
    public Coupon createCoupon(
            Coupon coupon) {

        validateCouponData(
                coupon,
                null);

        coupon.setCouponId(null);

        if (coupon.getStatus() == null ||
                coupon.getStatus().isBlank()) {

            coupon.setStatus(
                    "ACTIVE");
        }

        coupon.setCouponCode(
                coupon
                        .getCouponCode()
                        .trim()
                        .toUpperCase());

        return couponRepository.save(
                coupon);
    }

    // =====================================================
    // 修改優惠券
    // =====================================================

    @Transactional
    public Coupon updateCoupon(
            Integer couponId,
            Coupon updatedCoupon) {

        Coupon existingCoupon = findById(
                couponId);

        validateCouponData(
                updatedCoupon,
                couponId);

        existingCoupon.setCouponCode(
                updatedCoupon
                        .getCouponCode()
                        .trim()
                        .toUpperCase());

        existingCoupon.setCouponName(
                updatedCoupon
                        .getCouponName());

        existingCoupon.setDiscountType(
                updatedCoupon
                        .getDiscountType());

        existingCoupon.setDiscountValue(
                updatedCoupon
                        .getDiscountValue());

        existingCoupon.setMinimumAmount(
                updatedCoupon
                        .getMinimumAmount());

        existingCoupon.setStartDate(
                updatedCoupon
                        .getStartDate());

        existingCoupon.setEndDate(
                updatedCoupon
                        .getEndDate());

        existingCoupon.setStatus(
                updatedCoupon
                        .getStatus());

        return couponRepository.save(
                existingCoupon);
    }

    // =====================================================
    // 啟用 / 停用優惠券
    // =====================================================

    @Transactional
    public Coupon updateStatus(
            Integer couponId,
            String status) {

        Coupon coupon = findById(
                couponId);

        String normalizedStatus = status
                .trim()
                .toUpperCase();

        if (!"ACTIVE".equals(
                normalizedStatus)
                &&
                !"INACTIVE".equals(
                        normalizedStatus)) {

            throw new IllegalArgumentException(
                    "優惠券狀態只能是 ACTIVE 或 INACTIVE");
        }

        coupon.setStatus(
                normalizedStatus);

        return couponRepository.save(
                coupon);
    }

    // =====================================================
    // 驗證優惠券 + 計算折扣
    // =====================================================

    public CouponValidationResponse validateCoupon(
            String code,
            Integer amount) {

        if (code == null ||
                code.isBlank()) {

            throw new IllegalArgumentException(
                    "請輸入優惠券代碼");
        }

        if (amount == null ||
                amount < 0) {

            throw new IllegalArgumentException(
                    "商品金額錯誤");
        }

        Coupon coupon = couponRepository
                .findByCouponCode(
                        code
                                .trim()
                                .toUpperCase())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "優惠券不存在"));

        if (!"ACTIVE".equals(
                coupon.getStatus())) {

            throw new IllegalArgumentException(
                    "優惠券目前未啟用");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(
                coupon.getStartDate())) {

            throw new IllegalArgumentException(
                    "優惠券尚未開始使用");
        }

        if (now.isAfter(
                coupon.getEndDate())) {

            throw new IllegalArgumentException(
                    "優惠券已過期");
        }

        if (amount < coupon.getMinimumAmount()) {

            throw new IllegalArgumentException(
                    "未達優惠券最低消費金額");
        }

        int discountAmount = calculateDiscount(
                coupon,
                amount);

        int finalAmount = Math.max(
                0,
                amount -
                        discountAmount);

        return new CouponValidationResponse(
                coupon.getCouponId(),
                coupon.getCouponCode(),
                coupon.getCouponName(),
                amount,
                discountAmount,
                finalAmount);
    }

    // =====================================================
    // 計算折扣
    // =====================================================

    private int calculateDiscount(
            Coupon coupon,
            Integer amount) {

        int discountAmount;

        if ("PERCENT".equals(
                coupon.getDiscountType())) {

            discountAmount = amount
                    * coupon.getDiscountValue()
                    / 100;

        } else if ("FIXED".equals(
                coupon.getDiscountType())) {

            discountAmount = coupon.getDiscountValue();

        } else {

            throw new IllegalArgumentException(
                    "優惠券折扣類型錯誤");
        }

        return Math.min(
                discountAmount,
                amount);
    }

    // =====================================================
    // 新增 / 修改資料驗證
    // =====================================================

    private void validateCouponData(
            Coupon coupon,
            Integer currentCouponId) {

        if (coupon == null) {

            throw new IllegalArgumentException(
                    "優惠券資料不可為空");
        }

        if (coupon.getCouponCode() == null ||
                coupon.getCouponCode().isBlank()) {

            throw new IllegalArgumentException(
                    "優惠券代碼不可為空");
        }

        if (coupon.getCouponName() == null ||
                coupon.getCouponName().isBlank()) {

            throw new IllegalArgumentException(
                    "優惠券名稱不可為空");
        }

        String discountType = coupon
                .getDiscountType()
                .trim()
                .toUpperCase();

        if (!"PERCENT".equals(
                discountType)
                &&
                !"FIXED".equals(
                        discountType)) {

            throw new IllegalArgumentException(
                    "折扣類型只能是 PERCENT 或 FIXED");
        }

        coupon.setDiscountType(
                discountType);

        if (coupon.getDiscountValue() == null ||
                coupon.getDiscountValue() <= 0) {

            throw new IllegalArgumentException(
                    "折扣值必須大於 0");
        }

        if ("PERCENT".equals(
                discountType)
                &&
                coupon.getDiscountValue() >= 100) {

            throw new IllegalArgumentException(
                    "百分比折扣值必須小於 100");
        }

        if (coupon.getMinimumAmount() == null ||
                coupon.getMinimumAmount() < 0) {

            throw new IllegalArgumentException(
                    "最低消費金額不可小於 0");
        }

        if (coupon.getStartDate() == null ||
                coupon.getEndDate() == null) {

            throw new IllegalArgumentException(
                    "請設定優惠券開始與結束時間");
        }

        if (!coupon
                .getEndDate()
                .isAfter(
                        coupon.getStartDate())) {

            throw new IllegalArgumentException(
                    "優惠券結束時間必須晚於開始時間");
        }

        if (coupon.getStatus() != null
                &&
                !coupon.getStatus().isBlank()) {

            String status = coupon
                    .getStatus()
                    .trim()
                    .toUpperCase();

            if (!"ACTIVE".equals(
                    status)
                    &&
                    !"INACTIVE".equals(
                            status)) {

                throw new IllegalArgumentException(
                        "優惠券狀態只能是 ACTIVE 或 INACTIVE");
            }

            coupon.setStatus(
                    status);
        }

        String normalizedCode = coupon
                .getCouponCode()
                .trim()
                .toUpperCase();

        couponRepository
                .findByCouponCode(
                        normalizedCode)
                .ifPresent(
                        found -> {

                            if (currentCouponId == null
                                    ||
                                    !found
                                            .getCouponId()
                                            .equals(
                                                    currentCouponId)) {

                                throw new IllegalArgumentException(
                                        "優惠券代碼已存在");
                            }
                        });
    }
}