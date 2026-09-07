package com.hotel.model.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RoomTaskDTO {
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
