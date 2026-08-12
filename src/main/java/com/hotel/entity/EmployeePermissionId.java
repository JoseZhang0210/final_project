package com.hotel.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePermissionId implements Serializable {
    private Integer permissionId;
    private Integer employeeId;
}
