package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}
