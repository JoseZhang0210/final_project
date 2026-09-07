package com.hotel.model.dto;

import lombok.Data;

@Data
public class RoomImageDTO {
    private Integer imageId;
    private String path;
    private String imageDescription;
    private Integer roomTypeId;
}
