package com.hotel.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_permission")
@IdClass(EmployeePermissionId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePermission {

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @Id
    @Column(name = "permission_id")
    private Integer permissionId;
}

