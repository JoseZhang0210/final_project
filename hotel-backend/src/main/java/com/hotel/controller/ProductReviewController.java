package com.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.dto.ProductReviewDTO;
import com.hotel.model.dto.ProductReviewRequest;
import com.hotel.service.ProductReviewService;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    public ProductReviewController(ProductReviewService productReviewService) {
        this.productReviewService = productReviewService;
    }

    @GetMapping
    public ResponseEntity<List<ProductReviewDTO>> findByProduct(
            @PathVariable Integer productId,
            Authentication authentication) {
        return ResponseEntity.ok(
                productReviewService.findByProduct(productId, authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<ProductReviewDTO> save(
            @PathVariable Integer productId,
            @RequestBody ProductReviewRequest request,
            Authentication authentication) {
        ProductReviewDTO saved = productReviewService.save(
                productId,
                authentication.getName(),
                request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer productId,
            @PathVariable Integer reviewId,
            Authentication authentication) {
        boolean canManageReviews = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())
                        || "ROLE_EMPLOYEE".equals(authority.getAuthority()));
        boolean deleted = productReviewService.delete(
                productId,
                reviewId,
                authentication.getName(),
                canManageReviews);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.noContent().build();
    }
}
