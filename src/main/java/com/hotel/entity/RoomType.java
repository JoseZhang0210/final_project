package com.hotel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room_type")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id")
    private Integer roomTypeId;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "bed_type")
    private String bedType;

    @Column(name = "description")
    private String description;

    @Column(name = "price_per_night")
    private Integer pricePerNight;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "image_id")
    private Integer imageId;

}
