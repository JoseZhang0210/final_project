package com.hotel.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.web.multipart.MultipartFile;

import com.hotel.model.dto.EmployeeDTO;
import com.hotel.model.entity.Account;
import com.hotel.repository.AccountRepository;
import com.hotel.service.EmployeeService;
import com.hotel.util.JsonUtils;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final AccountRepository accountRepository;

    public EmployeeController(EmployeeService employeeService, AccountRepository accountRepository) {
        this.employeeService = employeeService;
        this.accountRepository = accountRepository;
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
            @RequestBody EmployeeDTO employeeDTO,
            Authentication authentication) {

        EmployeeDTO existingEmployee = employeeService.findById(id);
        if (existingEmployee == null) {
            return ResponseEntity.notFound().build();
        }

        // 阻止登入中的帳號將自己的狀態變更為停用
        String currentUsername = getAuthenticatedUsername(authentication);
        if (currentUsername != null && existingEmployee.getUsername() != null
                && currentUsername.equalsIgnoreCase(existingEmployee.getUsername())) {
            if (employeeDTO.getStatus() != null && !"1".equals(employeeDTO.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("message", "無法停用目前登入中的帳號"));
            }
        }

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
            @RequestParam String status,
            Authentication authentication) {

        EmployeeDTO existingEmployee = employeeService.findById(id);
        if (existingEmployee == null) {
            return ResponseEntity.notFound().build();
        }

        // 阻止登入中的帳號停用自己的帳號
        String currentUsername = getAuthenticatedUsername(authentication);
        if (currentUsername != null && existingEmployee.getUsername() != null
                && currentUsername.equalsIgnoreCase(existingEmployee.getUsername())) {
            if (!"1".equals(status)) {
                return ResponseEntity.badRequest().body(Map.of("message", "無法停用目前登入中的帳號"));
            }
        }

        EmployeeDTO updatedEmployee = employeeService.updateEmployeeStatus(id, status);

        if (updatedEmployee == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedEmployee);
    }

    private String getAuthenticatedUsername(Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            return authentication.getName();
        }
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        if (contextAuth != null && contextAuth.getName() != null) {
            return contextAuth.getName();
        }
        return null;
    }

    // =========================================
    // 7. 刪除員工
    // DELETE /api/employees/{id}
    // 例如：DELETE /api/employees/1
    // =========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(
            @PathVariable Integer id,
            Authentication authentication) {
        EmployeeDTO existingEmployee = employeeService.findById(id);

        if (existingEmployee == null) {
            return ResponseEntity.notFound().build();
        }

        // 阻止登入中的帳號刪除自己的帳號
        String currentUsername = getAuthenticatedUsername(authentication);
        if (currentUsername != null && existingEmployee.getUsername() != null
                && currentUsername.equalsIgnoreCase(existingEmployee.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "無法刪除目前登入中的帳號"));
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

    // =========================================
    // 8. JSON 匯出員工資料
    // GET /api/employees/export
    // 支援 RequestParam 調整匯出資料範圍：
    // - keyword: 關鍵字搜尋篩選
    // - status: 帳號狀態篩選 ("1" 啟用 / "0" 停用)
    // - departmentId: 部門篩選
    // - ids: 指定員工 ID 清單 (例如：ids=1&ids=2 或 ids=1,2,3)
    // - minId: 最小員工 ID
    // - maxId: 最大員工 ID
    // - limit: 匯出筆數限制
    // - offset: 匯出筆數偏移
    // - download: 是否以檔案附件形式下載 (預設 true)
    // =========================================
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportEmployees(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) List<Integer> ids,
            @RequestParam(required = false) Integer minId,
            @RequestParam(required = false) Integer maxId,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false, defaultValue = "true") boolean download) {

        List<EmployeeDTO> employees = employeeService.findAllEmployees(keyword, status, departmentId);

        // 依 ID 列表篩選範圍
        if (ids != null && !ids.isEmpty()) {
            employees = employees.stream()
                    .filter(e -> e.getEmployeeId() != null && ids.contains(e.getEmployeeId()))
                    .collect(Collectors.toList());
        }

        // 依 ID 區間篩選範圍
        if (minId != null) {
            employees = employees.stream()
                    .filter(e -> e.getEmployeeId() != null && e.getEmployeeId() >= minId)
                    .collect(Collectors.toList());
        }

        if (maxId != null) {
            employees = employees.stream()
                    .filter(e -> e.getEmployeeId() != null && e.getEmployeeId() <= maxId)
                    .collect(Collectors.toList());
        }

        // 依分頁/筆數調整範圍
        if (offset != null && offset > 0) {
            employees = employees.stream()
                    .skip(offset)
                    .collect(Collectors.toList());
        }

        if (limit != null && limit > 0) {
            employees = employees.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        // 依 accountId 批次查詢 Account 密碼
        List<Integer> accountIds = employees.stream()
                .map(EmployeeDTO::getAccountId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, String> passwordMap = new java.util.HashMap<>();
        if (!accountIds.isEmpty()) {
            List<Account> accounts = accountRepository.findAllById(accountIds);
            for (Account acc : accounts) {
                if (acc != null && acc.getAccountId() != null) {
                    passwordMap.put(acc.getAccountId(), acc.getPassword());
                }
            }
        }

        // 整理匯出資料：填入 password
        List<Map<String, Object>> exportDataList = new ArrayList<>();
        for (EmployeeDTO emp : employees) {
            String pwd = null;
            if (emp.getAccountId() != null) {
                pwd = passwordMap.get(emp.getAccountId());
            }
            if (pwd == null && emp.getUsername() != null) {
                Account acc = accountRepository.findByUsername(emp.getUsername().trim());
                if (acc != null) {
                    pwd = acc.getPassword();
                }
            }
            emp.setPassword(pwd);

            @SuppressWarnings("unchecked")
            Map<String, Object> map = JsonUtils.convert(emp, Map.class);
            if (map != null) {
                exportDataList.add(map);
            }
        }

        // 調用 JsonUtils 將員工列表序列化為美化格式 JSON 字串
        String json = JsonUtils.toPrettyJson(exportDataList);
        byte[] jsonBytes = (json != null ? json : "[]").getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (download) {
            headers.setContentDisposition(
                    ContentDisposition.attachment().filename("employees.json", StandardCharsets.UTF_8).build());
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(jsonBytes);
    }

    // =========================================
    // 9. JSON 匯入員工 (支援 JSON 字串 / 請求主體)
    // POST /api/employees/import
    // =========================================
    @PostMapping(value = {"/import", "/import/json"}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> importEmployeesFromJson(@RequestBody String json) {
        return processImport(json);
    }

    // =========================================
    // 10. JSON 匯入員工 (支援檔案上傳)
    // POST /api/employees/import (multipart/form-data)
    // =========================================
    @PostMapping(value = {"/import", "/import/file"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importEmployeesFromFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "請選擇要匯入的 JSON 檔案"));
        }
        try {
            String json = new String(file.getBytes(), StandardCharsets.UTF_8);
            return processImport(json);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "讀取檔案失敗：" + e.getMessage()));
        }
    }

    // =========================================
    // 匯入處理共用邏輯（調用 JsonUtils 反序列化並寫入員工資料）
    // =========================================
    private ResponseEntity<?> processImport(String json) {
        if (json == null || json.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "匯入的 JSON 內容不可為空"));
        }

        List<EmployeeDTO> employeeList;
        try {
            employeeList = JsonUtils.toList(json, EmployeeDTO.class);
        } catch (Exception e) {
            try {
                EmployeeDTO single = JsonUtils.fromJson(json, EmployeeDTO.class);
                if (single != null) {
                    employeeList = List.of(single);
                } else {
                    return ResponseEntity.badRequest().body(Map.of("message", "JSON 解析失敗：內容為空或格式錯誤"));
                }
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body(Map.of("message", "JSON 解析失敗：" + ex.getMessage()));
            }
        }

        if (employeeList == null || employeeList.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "未解析到任何員工資料"));
        }

        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        for (EmployeeDTO dto : employeeList) {
            if (dto == null) {
                continue;
            }
            if (dto.getUsername() == null || dto.getUsername().isBlank()) {
                failureCount++;
                errors.add("員工資料缺少帳號 (username)");
                continue;
            }

            try {
                String username = dto.getUsername().trim();
                EmployeeDTO existing = employeeService.findByUsername(username);

                if (existing == null && dto.getEmployeeId() != null) {
                    existing = employeeService.findById(dto.getEmployeeId());
                }

                String rawOrHashedPassword = dto.getPassword();
                boolean isAlreadyEncoded = rawOrHashedPassword != null &&
                        (rawOrHashedPassword.startsWith("$2a$") ||
                         rawOrHashedPassword.startsWith("$2b$") ||
                         rawOrHashedPassword.startsWith("$2y$"));

                if (existing != null) {
                    // 若密碼已是 BCrypt 雜湊，先清空 dto 密碼避免被 employeeService 二次編碼
                    if (isAlreadyEncoded) {
                        dto.setPassword(null);
                    }
                    employeeService.updateEmployee(existing.getEmployeeId(), dto);

                    // 覆寫回原本的 BCrypt 雜湊密碼
                    if (isAlreadyEncoded && existing.getAccountId() != null) {
                        Account acc = accountRepository.findById(existing.getAccountId()).orElse(null);
                        if (acc != null) {
                            acc.setPassword(rawOrHashedPassword);
                            accountRepository.save(acc);
                        }
                    }
                } else {
                    if (isAlreadyEncoded) {
                        dto.setPassword(null); // 先建立帳號（預設密碼）
                    }
                    EmployeeDTO created = employeeService.createEmployee(dto);

                    // 覆寫回原本的 BCrypt 雜湊密碼
                    if (isAlreadyEncoded && created != null && created.getAccountId() != null) {
                        Account acc = accountRepository.findById(created.getAccountId()).orElse(null);
                        if (acc != null) {
                            acc.setPassword(rawOrHashedPassword);
                            accountRepository.save(acc);
                        }
                    }
                }
                successCount++;
            } catch (Exception e) {
                failureCount++;
                errors.add("帳號 '" + dto.getUsername() + "' 匯入失敗：" + e.getMessage());
            }
        }

        return ResponseEntity.ok(Map.of(
                "message", "匯入處理完成",
                "total", employeeList.size(),
                "successCount", successCount,
                "failureCount", failureCount,
                "errors", errors));
    }
}
