package com.hotel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
public class Employee {

    @Id
    @Column(name = "employee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "account_id", nullable = false, unique = true)
    private Integer accountId;

    @Column(name = "position", nullable = false, length = 50)
    private String position;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin;

    public Employee(Integer departmentId, Integer accountId, String position, Boolean isAdmin) {
        this.departmentId = departmentId;
        this.accountId = accountId;
        this.position = position;
        this.isAdmin = isAdmin;
    }
}
