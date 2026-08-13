package com.hotel.dto;

import java.math.BigDecimal;

/**
 * 房間類型 (RoomType) DTO
 */
public class RoomTypeDTO {

    private Integer roomTypeId;
    private String roomTypeName;
    private String description;
    private Integer capacity;
    private BigDecimal price;
    private String status;

    public RoomTypeDTO() {
    }

    public RoomTypeDTO(Integer roomTypeId, String roomTypeName, String description, Integer capacity, BigDecimal price,
            String status) {
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.description = description;
        this.capacity = capacity;
        this.price = price;
        this.status = status;
    }

    // Getter & Setter
    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
