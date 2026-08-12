package com.hotel.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
public class Profile {

    @Id
    @Column(name = "profile_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "zipcode", length = 10)
    private String zipcode;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "district", length = 50)
    private String district;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Profile(Account account, String name, String email, String phone, String zipcode,
            String city, String district, String address, LocalDateTime createdAt,
            LocalDate birthday, String gender, LocalDateTime updatedAt) {
        this.account = account;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.zipcode = zipcode;
        this.city = city;
        this.district = district;
        this.address = address;
        this.createdAt = createdAt;
        this.birthday = birthday;
        this.gender = gender;
        this.updatedAt = updatedAt;
    }
}
