package com.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

        Optional<Employee> findByDepartmentId(Integer departmentId);
        Optional<Employee> findByAccountId(Integer accountId);

}
