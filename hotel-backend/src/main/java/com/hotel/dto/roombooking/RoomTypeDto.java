package com.hotel.dto.roombooking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeDto {

    private Integer roomTypeId;
    private String typeName;
    private String bedType;
    private String description;
    private Integer pricePerNight;
    private Integer capacity;
    private Integer imageId;

}