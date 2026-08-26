package com.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    List<Employee> findByDepartmentId(Integer departmentId);

    Optional<Employee> findByAccountId(Integer accountId);

    void deleteByAccountId(Integer accountId);
}
