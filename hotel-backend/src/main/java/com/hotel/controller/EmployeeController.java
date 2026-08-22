package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.dto.EmployeeDTO;
import com.hotel.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // =========================================
    // 1. 查詢員工列表（支援關鍵字、狀態與部門篩選）
    // GET /api/employees
    // 例如：GET /api/employees
    //      GET /api/employees?keyword=經理
    //      GET /api/employees?status=1
    //      GET /api/employees?departmentId=1
    // =========================================
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> findAllEmployees(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer departmentId) {

        List<EmployeeDTO> employees = employeeService.findAllEmployees(keyword, status, departmentId);
        return ResponseEntity.ok(employees);
    }

    // =========================================
    // 2. 關鍵字搜尋員工 (別名端點)
    // GET /api/employees/search
    // 例如：GET /api/employees/search?keyword=經理
    // =========================================
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDTO>> searchEmployees(@RequestParam String keyword) {
        List<EmployeeDTO> employees = employeeService.findAllEmployees(keyword, null, null);
        return ResponseEntity.ok(employees);
    }

    // =========================================
    // 3. 依 ID 查詢單一員工詳細資料
    // GET /api/employees/{id}
    // 例如：GET /api/employees/1
    // =========================================
    @GetMapping("/{id}")
    public ResponseEntity<?> findEmployeeById(@PathVariable Integer id) {
        EmployeeDTO employee = employeeService.findById(id);

        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(employee);
    }

    // =========================================
    // 4. 後台新增員工
    // POST /api/employees
    // =========================================
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "建立員工失敗：" + e.getMessage()));
        }
    }

    // =========================================
    // 5. 修改員工詳細資料
    // PUT /api/employees/{id}
    // 例如：PUT /api/employees/1
    // =========================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable Integer id,
            @RequestBody EmployeeDTO employeeDTO) {

        EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);

        if (updatedEmployee == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedEmployee);
    }

    // =========================================
    // 6. 快速切換員工帳號狀態 (啟用 "1" / 停用 "0")
    // PATCH /api/employees/{id}/status
    // 例如：PATCH /api/employees/1/status?status=0
    // =========================================
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateEmployeeStatus(
            @PathVariable Integer id,
            @RequestParam String status) {

        EmployeeDTO updatedEmployee = employeeService.updateEmployeeStatus(id, status);

        if (updatedEmployee == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedEmployee);
    }

    // =========================================
    // 7. 刪除員工
    // DELETE /api/employees/{id}
    // 例如：DELETE /api/employees/1
    // =========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Integer id) {
        EmployeeDTO existingEmployee = employeeService.findById(id);

        if (existingEmployee == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            boolean deleted = employeeService.deleteEmployee(id);

            if (!deleted) {
                return ResponseEntity.notFound().build();
            }

            /*
             * 204 No Content
             * 代表刪除成功，無回傳內容
             */
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            /*
             * 若該員工已有相關關聯紀錄，回傳 409 Conflict
             */
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "無法刪除：該員工已有相關業務或系統紀錄。建議將帳號狀態變更為停用。"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "刪除員工失敗：" + e.getMessage()));
        }
    }
}

