package com.hotel.controller;

import java.util.List;
import java.util.Map;

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

import com.hotel.model.entity.Permission;
import com.hotel.repository.EmployeePermissionRepository;
import com.hotel.repository.PermissionRepository;

@RestController
@RequestMapping("/api/permissions")
@PreAuthorize("hasAuthority('EMPLOYEE_MANAGE') or hasAuthority('POSITION_總經理') or hasAuthority('ROLE_EMPLOYEE')")
public class PermissionController {

    private final PermissionRepository permissionRepository;
    private final EmployeePermissionRepository employeePermissionRepository;

    public PermissionController(
            PermissionRepository permissionRepository,
            EmployeePermissionRepository employeePermissionRepository) {
        this.permissionRepository = permissionRepository;
        this.employeePermissionRepository = employeePermissionRepository;
    }

    // =========================================
    // 1. 查詢全部權限種類
    // GET /api/permissions
    // =========================================
    @GetMapping
    public List<Permission> findAllPermissions() {
        return permissionRepository.findAllByOrderByPermissionIdAsc();
    }

    // =========================================
    // 2. 新增權限種類
    // POST /api/permissions
    // =========================================
    @PostMapping
    public ResponseEntity<?> createPermission(@RequestBody Permission permission) {
        String permissionName = permission.getPermissionName();
        String permissionCode = permission.getPermissionCode();

        if (permissionName == null || permissionName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "權限名稱不能為空"));
        }
        if (permissionCode == null || permissionCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "權限代碼不能為空"));
        }

        permissionName = permissionName.trim();
        permissionCode = permissionCode.trim().toUpperCase();

        if (permissionRepository.existsByPermissionCodeIgnoreCase(permissionCode)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "權限代碼已存在"));
        }

        if (permissionRepository.existsByPermissionNameIgnoreCase(permissionName)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "權限名稱已存在"));
        }

        permission.setPermissionId(null);
        permission.setPermissionName(permissionName);
        permission.setPermissionCode(permissionCode);

        Permission saved = permissionRepository.save(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // =========================================
    // 3. 修改權限種類
    // PUT /api/permissions/{id}
    // =========================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermission(
            @PathVariable Integer id,
            @RequestBody Permission formPermission) {

        Permission existingPermission = permissionRepository.findById(id).orElse(null);
        if (existingPermission == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "找不到權限種類"));
        }

        String permissionName = formPermission.getPermissionName();
        String permissionCode = formPermission.getPermissionCode();

        if (permissionName == null || permissionName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "權限名稱不能為空"));
        }
        if (permissionCode == null || permissionCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "權限代碼不能為空"));
        }

        permissionName = permissionName.trim();
        permissionCode = permissionCode.trim().toUpperCase();

        Permission sameCodePermission = permissionRepository.findByPermissionCodeIgnoreCase(permissionCode).orElse(null);
        if (sameCodePermission != null && !sameCodePermission.getPermissionId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "權限代碼已存在"));
        }

        Permission sameNamePermission = permissionRepository.findByPermissionNameIgnoreCase(permissionName).orElse(null);
        if (sameNamePermission != null && !sameNamePermission.getPermissionId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "權限名稱已存在"));
        }

        existingPermission.setPermissionName(permissionName);
        existingPermission.setPermissionCode(permissionCode);

        Permission updated = permissionRepository.save(existingPermission);
        return ResponseEntity.ok(updated);
    }

    // =========================================
    // 4. 刪除權限種類
    // DELETE /api/permissions/{id}
    // =========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable Integer id) {
        Permission permission = permissionRepository.findById(id).orElse(null);
        if (permission == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "找不到權限種類"));
        }

        boolean used = employeePermissionRepository.existsByPermissionId(id);
        if (used) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "此權限種類仍有員工使用，請先移除員工權限後再刪除"));
        }

        try {
            permissionRepository.delete(permission);
            permissionRepository.flush();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "此權限種類仍被其他資料使用，無法刪除"));
        }
    }
}

