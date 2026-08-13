package com.hotel.dto;

import java.math.BigDecimal;

/**
 * 租賃 (Rental) DTO
 */
public class RentalDTO {

    private Integer rentalId;
    private String rentalName;
    private String description;
    private BigDecimal dailyRate;
    private Integer quantity;
    private String status;

    public RentalDTO() {
    }

    public RentalDTO(Integer rentalId, String rentalName, String description, BigDecimal dailyRate, Integer quantity,
            String status) {
        this.rentalId = rentalId;
        this.rentalName = rentalName;
        this.description = description;
        this.dailyRate = dailyRate;
        this.quantity = quantity;
        this.status = status;
    }

    // Getter & Setter
    public Integer getRentalId() {
        return rentalId;
    }

    public void setRentalId(Integer rentalId) {
        this.rentalId = rentalId;
    }

    public String getRentalName() {
        return rentalName;
    }

    public void setRentalName(String rentalName) {
        this.rentalName = rentalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
