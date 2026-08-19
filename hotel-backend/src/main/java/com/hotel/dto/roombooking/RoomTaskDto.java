package com.hotel.dto.roombooking;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTaskDto {

    private Integer taskId;
    private Integer roomId;
    private Integer employeeId;
    private String remark;
    private String priority;
    private Date createdAt;
    private Date completedAt;
    private String taskType;
    private String taskStatus;

}