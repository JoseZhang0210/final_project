package com.hotel.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskId;
    private Integer roomId;
    private Integer employeeId;
    private String priority;
    private String taskType;
    private String taskStatus;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
