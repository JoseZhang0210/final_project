package com.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    List<ProductReview> findByProductIdOrderByUpdatedAtDesc(Integer productId);

    Optional<ProductReview> findByProductIdAndMemberId(Integer productId, Integer memberId);

    Optional<ProductReview> findByReviewIdAndProductId(Integer reviewId, Integer productId);

    long deleteByProductId(Integer productId);
}
