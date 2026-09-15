package com.hotel.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Department;
import com.hotel.repository.DepartmentRepository;
import com.hotel.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/departments")
@PreAuthorize("hasAuthority('EMPLOYEE_MANAGE') or hasAuthority('POSITION_總經理') or hasAuthority('ROLE_EMPLOYEE')")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentController(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    // =========================================
    // 1. 查詢所有部門列表
    // GET /api/departments
    // =========================================
    @GetMapping
    public ResponseEntity<List<Department>> findAllDepartments() {
        List<Department> departments = departmentRepository.findAllByOrderByDepartmentIdAsc();
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
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // =========================================
    // 3. 新增部門
    // POST /api/departments
    // =========================================
    @PostMapping
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        if (department.getDepartmentName() == null || department.getDepartmentName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "部門名稱不得為空"));
        }

        String name = department.getDepartmentName().trim();
        if (departmentRepository.existsByDepartmentNameIgnoreCase(name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "部門名稱已存在"));
        }

        Department newDept = new Department();
        newDept.setDepartmentName(name);
        Department saved = departmentRepository.save(newDept);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // =========================================
    // 4. 修改部門
    // PUT /api/departments/{id}
    // =========================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable Integer id,
            @RequestBody Department formDepartment) {

        Department existingDepartment = departmentRepository.findById(id).orElse(null);
        if (existingDepartment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "找不到部門"));
        }

        if (formDepartment.getDepartmentName() == null || formDepartment.getDepartmentName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "部門名稱不得為空"));
        }

        String name = formDepartment.getDepartmentName().trim();
        Optional<Department> sameNameDept = departmentRepository.findByDepartmentNameIgnoreCase(name);
        if (sameNameDept.isPresent() && !sameNameDept.get().getDepartmentId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "部門名稱已存在"));
        }

        existingDepartment.setDepartmentName(name);
        Department updated = departmentRepository.save(existingDepartment);
        return ResponseEntity.ok(updated);
    }

    // =========================================
    // 5. 刪除部門
    // DELETE /api/departments/{id}
    // =========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Integer id) {
        Department department = departmentRepository.findById(id).orElse(null);
        if (department == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "找不到部門"));
        }

        boolean used = employeeRepository.existsByDepartmentId(id);
        if (used) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "此部門仍有員工使用，請先調整員工所屬部門後再刪除"));
        }

        try {
            departmentRepository.delete(department);
            departmentRepository.flush();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "此部門仍被其他資料使用，無法刪除"));
        }
    }
}


