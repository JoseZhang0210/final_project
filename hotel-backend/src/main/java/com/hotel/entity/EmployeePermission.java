package com.hotel.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee_permission")
@Getter
@Setter
@NoArgsConstructor
public class EmployeePermission {

    @EmbeddedId
    private EmployeePermissionId id = new EmployeePermissionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public EmployeePermission(Permission permission, Employee employee) {
        this.permission = permission;
        this.employee = employee;
        if (permission != null) {
            this.id.setPermissionId(permission.getId());
        }
        if (employee != null) {
            this.id.setEmployeeId(employee.getId());
        }
    }
}
