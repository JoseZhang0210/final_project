package com.hotel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_permission")
@IdClass(EmployeePermissionId.class)
@Data
@NoArgsConstructor
public class EmployeePermission {

    @Id
    @Column(name = "permission_id")
    private Integer permissionId;

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    public EmployeePermission(Integer permissionId, Integer employeeId) {
        this.permissionId = permissionId;
        this.employeeId = employeeId;
    }
}
