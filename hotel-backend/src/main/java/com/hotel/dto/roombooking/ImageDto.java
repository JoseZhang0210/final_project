package com.hotel.dto.roombooking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {

    private Integer imageId;
    private String path;
    private String imageDesc;

}