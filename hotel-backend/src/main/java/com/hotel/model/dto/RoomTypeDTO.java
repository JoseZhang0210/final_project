package com.hotel.model.dto;

import lombok.Data;

@Data
public class RoomTypeDTO {
    private Integer roomTypeId;
    private String typeName;
    private String bedType;
    private Integer capacity;
    private String roomDescription;
    private Integer pricePerNight;
    private Integer availableRooms;
}
