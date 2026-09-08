package com.hotel.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductReviewDTO {

    private Integer reviewId;
    private Integer rating;
    private String comment;
    private String memberName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean ownReview;
}
