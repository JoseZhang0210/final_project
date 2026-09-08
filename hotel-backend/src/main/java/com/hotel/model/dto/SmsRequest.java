package com.hotel.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsRequest {

    private String phone;
    private String message;
}
