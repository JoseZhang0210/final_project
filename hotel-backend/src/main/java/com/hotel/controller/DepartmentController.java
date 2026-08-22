package com.hotel.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Department;
import com.hotel.repository.DepartmentRepository;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    // =========================================
    // 1. 查詢所有部門列表
    // GET /api/departments
    // =========================================
    @GetMapping
    public ResponseEntity<List<Department>> findAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return ResponseEntity.ok(departments);
    }

    // =========================================
    // 2. 依 ID 查詢部門
    // GET /api/departments/{id}
    // =========================================
    @GetMapping("/{id}")
    public ResponseEntity<?> findDepartmentById(@PathVariable Integer id) {
        return departmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =========================================
    // 3. 新增部門（若已存在同名部門則直接回傳既有部門）
    // POST /api/departments
    // =========================================
    @PostMapping
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        if (department.getDepartmentName() == null || department.getDepartmentName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "部門名稱不得為空"));
        }

        String name = department.getDepartmentName().trim();
        Optional<Department> existing = departmentRepository.findByDepartmentName(name);
        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get());
        }

        Department newDept = new Department();
        newDept.setDepartmentName(name);
        Department saved = departmentRepository.save(newDept);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}

