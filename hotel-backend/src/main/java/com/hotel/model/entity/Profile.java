package com.hotel.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer profileId;
    private Integer accountId;
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
