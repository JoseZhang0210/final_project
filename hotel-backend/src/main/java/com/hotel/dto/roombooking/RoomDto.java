package com.hotel.dto.roombooking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {

    private Integer roomId;
    private String roomNumber;
    private Integer roomTypeId;
    private Integer floor;
    private String roomStatus;

}