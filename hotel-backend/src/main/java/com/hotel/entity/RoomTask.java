package com.hotel.entity;

import java.util.Date;

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
@Table(name = "room_task")
public class RoomTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "remark")
    private String remark;

    @Column(name = "priority")
    private String priority;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "completed_at",nullable = true)
    private Date completedAt;

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "task_status")
    private String taskStatus;

}
