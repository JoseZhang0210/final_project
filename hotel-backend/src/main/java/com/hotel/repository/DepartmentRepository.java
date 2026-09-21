package com.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    List<Department> findAllByOrderByDepartmentIdAsc();

    Optional<Department> findByDepartmentName(String departmentName);

    Optional<Department> findByDepartmentNameIgnoreCase(String departmentName);

    boolean existsByDepartmentName(String departmentName);

    boolean existsByDepartmentNameIgnoreCase(String departmentName);
}


