package com.hotel.model.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePermissionId implements Serializable {

    private Integer employeeId;
    private Integer permissionId;
}

