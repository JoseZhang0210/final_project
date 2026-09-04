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
    private Integer availableRooms; // 原始設定房間總數
    private Integer todayAvailableRooms; // 今日剩餘可用房間數
    private String mainImageUrl; // 房型主圖 (封面圖)
}
