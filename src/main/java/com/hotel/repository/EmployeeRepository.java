package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
