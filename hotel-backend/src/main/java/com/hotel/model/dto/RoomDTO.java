package com.hotel.model.dto;

import lombok.Data;

@Data
public class RoomDTO {
    private Integer roomId;
    private String roomNumber;
    private Integer roomTypeId;
    private Integer floor;
    private String roomStatus;
}
