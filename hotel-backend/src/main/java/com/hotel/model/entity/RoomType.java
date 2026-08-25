package com.hotel.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "room", "roomImage", "booking" })
@EqualsAndHashCode(exclude = { "room", "roomImage", "booking" })
@Table(name = "room_type")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id")
    private Integer roomTypeId;

    @Column(name = "type_name", nullable = false, length = 20)
    private String typeName;

    @Column(name = "bed_type", nullable = false, length = 20)
    private String bedType;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "room_description", nullable = true)
    private String roomDescription;

    @Column(name = "price_per_night", nullable = false)
    private Integer pricePerNight;

    // =================以下多關聯性至Room,RoomImage,Booking

    // 1:N 關聯至 Room
    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> room = new ArrayList<>();

    // 1:N 關聯至 RoomImage
    @OneToMany(mappedBy = "roomType")
    private List<RoomImage> roomImage = new ArrayList<>();

    // 1:N 關聯至 Booking

    @OneToMany(mappedBy = "roomType")
    private List<Booking> booking = new ArrayList<>();

}
