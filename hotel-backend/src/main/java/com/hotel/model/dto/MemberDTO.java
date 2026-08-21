package com.hotel.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    // Member 表
    private Integer memberId;

    // Account 表
    private Integer accountId;
    private String username;
    private String password;
    private String status;

    // Profile 表
    private Integer profileId;
    private String name;
    private String email;
    private String phone;
    private String zipcode;
    private String city;
    private String district;
    private String address;
    private LocalDateTime createdAt;
    private LocalDate birthday;
    private String gender;
    private LocalDateTime updatedAt;
}
