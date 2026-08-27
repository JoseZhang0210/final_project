package com.hotel.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@ToString(exclude = { "roomType", "roomTasks", "bookings" })
@EqualsAndHashCode(exclude = { "roomType", "roomTasks", "bookings" })
@Table(name = "room")
public class Room {

    @Id
    @Column(name = "room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @Column(name = "room_status", nullable = false, length = 20)
    private String roomStatus;

    // N:1 關聯至 RoomType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    // ===============多關聯性 RoomTask,RoomType

    // 1:N 關聯至 RoomTask
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomTask> roomTasks = new ArrayList<>();

    // 1:N 關聯至 Booking (一個房間可以有多筆預約紀錄)

    @OneToMany(mappedBy = "room")
    private List<Booking> bookings = new ArrayList<>();
}
