package com.hotel.model.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@ToString(exclude = { "room", "employee" })
@EqualsAndHashCode(exclude = { "room", "employee" })
@Table(name = "room_task")
public class RoomTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    // 外鍵指向 Room (N:1 關係)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    @JsonIgnore
    private Room room;

    @Column(name = "employee_id")
    private Integer employeeId;

    // 外鍵指向 Emplyee (N:1 關係)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    @JsonIgnore
    private Employee employee;

    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Column(name = "task_type", nullable = false, length = 20)
    private String taskType;

    @Column(name = "task_status", nullable = false, length = 20)
    private String taskStatus;

    @Column(name = "remark", nullable = true, length = 100)
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm[:ss]", timezone = "GMT+8")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm[:ss]", timezone = "GMT+8")
    @Column(name = "completed_at", nullable = true)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

}
