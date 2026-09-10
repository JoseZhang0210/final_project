package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.model.entity.EmployeePermission;
import com.hotel.model.entity.EmployeePermissionId;

@Repository
public interface EmployeePermissionRepository extends JpaRepository<EmployeePermission, EmployeePermissionId> {

    List<EmployeePermission> findByEmployeeId(Integer employeeId);

    List<EmployeePermission> findByEmployeeIdIn(List<Integer> employeeIds);

    void deleteByEmployeeId(Integer employeeId);

    boolean existsByPermissionId(Integer permissionId);

    void deleteByPermissionId(Integer permissionId);
}

