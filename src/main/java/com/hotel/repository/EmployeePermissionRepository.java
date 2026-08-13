package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.EmployeePermission;
import com.hotel.entity.EmployeePermissionId;

public interface EmployeePermissionRepository extends JpaRepository<EmployeePermission, EmployeePermissionId> {
}
