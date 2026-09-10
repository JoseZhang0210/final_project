package com.hotel.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.EmployeeDTO;
import com.hotel.model.entity.Account;
import com.hotel.model.entity.Department;
import com.hotel.model.entity.Employee;
import com.hotel.model.entity.EmployeePermission;
import com.hotel.model.entity.Permission;
import com.hotel.model.entity.Profile;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.DepartmentRepository;
import com.hotel.repository.EmployeePermissionRepository;
import com.hotel.repository.EmployeeRepository;
import com.hotel.repository.PermissionRepository;
import com.hotel.repository.ProfileRepository;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeePermissionRepository employeePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            AccountRepository accountRepository,
            ProfileRepository profileRepository,
            DepartmentRepository departmentRepository,
            EmployeePermissionRepository employeePermissionRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.departmentRepository = departmentRepository;
        this.employeePermissionRepository = employeePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================
    // 1. 查詢所有員工（整合 Account, Profile, Department 與 Permissions，支援關鍵字、狀態與部門篩選）
    // =========================================
    @Transactional(readOnly = true)
    public List<EmployeeDTO> findAllEmployees(String keyword, String status, Integer departmentId) {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDTO> list = new ArrayList<>();

        Map<Integer, String> permissionNameMap = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(Permission::getPermissionId, Permission::getPermissionName, (v1, v2) -> v1));

        List<EmployeePermission> allEmployeePermissions = employeePermissionRepository.findAll();
        Map<Integer, List<Integer>> empPermMap = new java.util.HashMap<>();
        for (EmployeePermission ep : allEmployeePermissions) {
            empPermMap.computeIfAbsent(ep.getEmployeeId(), k -> new ArrayList<>()).add(ep.getPermissionId());
        }

        for (Employee employee : employees) {
            Account account = null;
            if (employee.getAccountId() != null) {
                account = accountRepository.findById(employee.getAccountId()).orElse(null);
            }

            Profile profile = null;
            if (employee.getAccountId() != null) {
                profile = profileRepository.findByAccountId(employee.getAccountId()).orElse(null);
            }

            Department department = null;
            if (employee.getDepartmentId() != null) {
                department = departmentRepository.findById(employee.getDepartmentId()).orElse(null);
            }

            EmployeeDTO dto = toDTO(employee, account, profile, department);
            List<Integer> permIds = empPermMap.getOrDefault(employee.getEmployeeId(), Collections.emptyList());
            dto.setPermissionIds(new ArrayList<>(permIds));
            List<String> permNames = permIds.stream()
                    .map(permissionNameMap::get)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
            dto.setPermissionNames(permNames);

            list.add(dto);
        }

        // 篩選 status
        if (status != null && !status.isBlank()) {
            list = list.stream()
                    .filter(e -> status.equals(e.getStatus()))
                    .collect(Collectors.toList());
        }

        // 篩選 departmentId
        if (departmentId != null) {
            list = list.stream()
                    .filter(e -> departmentId.equals(e.getDepartmentId()))
                    .collect(Collectors.toList());
        }

        // 搜尋 keyword (比對 username, name, email, phone, position, departmentName)
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();
            list = list.stream()
                    .filter(e -> (e.getUsername() != null && e.getUsername().toLowerCase().contains(kw)) ||
                            (e.getName() != null && e.getName().toLowerCase().contains(kw)) ||
                            (e.getEmail() != null && e.getEmail().toLowerCase().contains(kw)) ||
                            (e.getPhone() != null && e.getPhone().contains(kw)) ||
                            (e.getPosition() != null && e.getPosition().toLowerCase().contains(kw)) ||
                            (e.getDepartmentName() != null && e.getDepartmentName().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }

        return list;
    }

    // =========================================
    // 2. 依 ID 查詢單一員工詳細資料
    // =========================================
    @Transactional(readOnly = true)
    public EmployeeDTO findById(Integer employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            return null;
        }

        Account account = null;
        if (employee.getAccountId() != null) {
            account = accountRepository.findById(employee.getAccountId()).orElse(null);
        }

        Profile profile = null;
        if (employee.getAccountId() != null) {
            profile = profileRepository.findByAccountId(employee.getAccountId()).orElse(null);
        }

        Department department = null;
        if (employee.getDepartmentId() != null) {
            department = departmentRepository.findById(employee.getDepartmentId()).orElse(null);
        }

        EmployeeDTO dto = toDTO(employee, account, profile, department);
        populatePermissions(dto, employee.getEmployeeId());
        return dto;
    }

    // =========================================
    // 2-1. 依使用者帳號查詢員工詳細資料
    // =========================================
    @Transactional(readOnly = true)
    public EmployeeDTO findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        Account account = accountRepository.findByUsername(username.trim());
        if (account == null || account.getAccountId() == null) {
            return null;
        }
        Employee employee = employeeRepository.findByAccountId(account.getAccountId()).orElse(null);
        if (employee == null) {
            return null;
        }
        Profile profile = profileRepository.findByAccountId(account.getAccountId()).orElse(null);
        Department department = null;
        if (employee.getDepartmentId() != null) {
            department = departmentRepository.findById(employee.getDepartmentId()).orElse(null);
        }
        EmployeeDTO dto = toDTO(employee, account, profile, department);
        populatePermissions(dto, employee.getEmployeeId());
        return dto;
    }


    // =========================================
    // 3. 新增員工（同步建立 Account, Employee, Profile）
    // =========================================
    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new IllegalArgumentException("使用者帳號不得為空");
        }

        // 檢查帳號是否已存在
        Account existingAccount = accountRepository.findByUsername(dto.getUsername().trim());
        if (existingAccount != null) {
            throw new DataIntegrityViolationException("使用者帳號 '" + dto.getUsername() + "' 已被註冊");
        }

        // 1. 建立 Account
        Account account = new Account();
        account.setUsername(dto.getUsername().trim());
        String rawPassword = (dto.getPassword() != null && !dto.getPassword().isBlank()) ? dto.getPassword() : "123456";
        account.setPassword(passwordEncoder.encode(rawPassword));
        account.setStatus((dto.getStatus() != null && !dto.getStatus().isBlank()) ? dto.getStatus() : "1");
        Account savedAccount = accountRepository.save(account);

        // 2. 處理部門（若指定的 departmentId 不存在或傳入 departmentName，自動建立）
        Department department = resolveOrCreateDepartment(dto.getDepartmentId(), dto.getDepartmentName());
        Integer resolvedDeptId = department != null ? department.getDepartmentId() : dto.getDepartmentId();

        // 3. 建立 Employee
        Employee employee = new Employee();
        employee.setAccountId(savedAccount.getAccountId());
        employee.setDepartmentId(resolvedDeptId);
        employee.setPosition(dto.getPosition());
        Employee savedEmployee = employeeRepository.save(employee);

        // 4. 建立 Profile
        Profile profile = new Profile();
        profile.setAccountId(savedAccount.getAccountId());
        profile.setName(dto.getName() != null && !dto.getName().isBlank() ? dto.getName() : dto.getUsername());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setZipcode(dto.getZipcode());
        profile.setCity(dto.getCity());
        profile.setDistrict(dto.getDistrict());
        profile.setAddress(dto.getAddress());
        profile.setBirthday(dto.getBirthday());
        profile.setGender(dto.getGender());
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        Profile savedProfile = profileRepository.save(profile);

        if (department == null && savedEmployee.getDepartmentId() != null) {
            department = departmentRepository.findById(savedEmployee.getDepartmentId()).orElse(null);
        }

        if (dto.getPermissionIds() != null) {
            syncEmployeePermissions(savedEmployee.getEmployeeId(), dto.getPermissionIds());
        }

        EmployeeDTO createdDto = toDTO(savedEmployee, savedAccount, savedProfile, department);
        populatePermissions(createdDto, savedEmployee.getEmployeeId());
        return createdDto;
    }

    // =========================================
    // 4. 修改員工詳細資料（含權限關聯同步）
    // =========================================
    public EmployeeDTO updateEmployee(Integer employeeId, EmployeeDTO dto) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            return null;
        }

        // 更新 Employee 欄位（含部門自動解析與建立）
        if (dto.getDepartmentId() != null || (dto.getDepartmentName() != null && !dto.getDepartmentName().isBlank())) {
            Department resolvedDept = resolveOrCreateDepartment(dto.getDepartmentId(), dto.getDepartmentName());
            if (resolvedDept != null) {
                employee.setDepartmentId(resolvedDept.getDepartmentId());
            } else if (dto.getDepartmentId() != null) {
                employee.setDepartmentId(dto.getDepartmentId());
            }
        }
        if (dto.getPosition() != null) {
            employee.setPosition(dto.getPosition());
        }
        employee = employeeRepository.save(employee);

        // 更新 Account 欄位
        Account account = null;
        if (employee.getAccountId() != null) {
            account = accountRepository.findById(employee.getAccountId()).orElse(null);
        }

        if (account != null) {
            if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
                account.setStatus(dto.getStatus());
            }
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                account.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            account = accountRepository.save(account);
        }

        // 更新 Profile 欄位
        Profile profile = null;
        if (employee.getAccountId() != null) {
            profile = profileRepository.findByAccountId(employee.getAccountId()).orElse(null);
        }

        if (profile == null && employee.getAccountId() != null) {
            profile = new Profile();
            profile.setAccountId(employee.getAccountId());
            profile.setCreatedAt(LocalDateTime.now());
        }

        if (profile != null) {
            if (dto.getName() != null)
                profile.setName(dto.getName());
            if (dto.getEmail() != null)
                profile.setEmail(dto.getEmail());
            if (dto.getPhone() != null)
                profile.setPhone(dto.getPhone());
            if (dto.getZipcode() != null)
                profile.setZipcode(dto.getZipcode());
            if (dto.getCity() != null)
                profile.setCity(dto.getCity());
            if (dto.getDistrict() != null)
                profile.setDistrict(dto.getDistrict());
            if (dto.getAddress() != null)
                profile.setAddress(dto.getAddress());
            if (dto.getBirthday() != null)
                profile.setBirthday(dto.getBirthday());
            if (dto.getGender() != null)
                profile.setGender(dto.getGender());
            profile.setUpdatedAt(LocalDateTime.now());
            profile = profileRepository.save(profile);
        }

        // 同步權限
        if (dto.getPermissionIds() != null) {
            syncEmployeePermissions(employee.getEmployeeId(), dto.getPermissionIds());
        }

        Department department = null;
        if (employee.getDepartmentId() != null) {
            department = departmentRepository.findById(employee.getDepartmentId()).orElse(null);
        }

        EmployeeDTO updatedDto = toDTO(employee, account, profile, department);
        populatePermissions(updatedDto, employee.getEmployeeId());
        return updatedDto;
    }

    // =========================================
    // 5. 快速更新員工帳號狀態 (啟用/停用)
    // =========================================
    public EmployeeDTO updateEmployeeStatus(Integer employeeId, String status) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            return null;
        }

        Account account = null;
        if (employee.getAccountId() != null) {
            account = accountRepository.findById(employee.getAccountId()).orElse(null);
        }

        if (account != null) {
            account.setStatus(status);
            account = accountRepository.save(account);
        }

        Profile profile = null;
        if (employee.getAccountId() != null) {
            profile = profileRepository.findByAccountId(employee.getAccountId()).orElse(null);
        }

        Department department = null;
        if (employee.getDepartmentId() != null) {
            department = departmentRepository.findById(employee.getDepartmentId()).orElse(null);
        }

        EmployeeDTO statusDto = toDTO(employee, account, profile, department);
        populatePermissions(statusDto, employee.getEmployeeId());
        return statusDto;
    }

    // =========================================
    // 6. 刪除員工（連動刪除 EmployeePermission, Profile, Employee, Account）
    // =========================================
    public boolean deleteEmployee(Integer employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            return false;
        }

        Integer accountId = employee.getAccountId();

        // 0. 刪除 EmployeePermission
        employeePermissionRepository.deleteByEmployeeId(employeeId);

        // 1. 刪除 Profile
        if (accountId != null) {
            profileRepository.deleteByAccountId(accountId);
        }

        // 2. 刪除 Employee
        employeeRepository.delete(employee);

        // 3. 刪除 Account
        if (accountId != null) {
            accountRepository.deleteById(accountId);
        }

        return true;
    }

    // =========================================
    // 輔助方法：同步更新員工權限關聯
    // =========================================
    private void syncEmployeePermissions(Integer employeeId, List<Integer> permissionIds) {
        if (employeeId == null) {
            return;
        }
        employeePermissionRepository.deleteByEmployeeId(employeeId);
        employeePermissionRepository.flush();

        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<EmployeePermission> list = new ArrayList<>();
            for (Integer permId : permissionIds) {
                if (permId != null) {
                    list.add(new EmployeePermission(employeeId, permId));
                }
            }
            if (!list.isEmpty()) {
                employeePermissionRepository.saveAll(list);
            }
        }
    }

    // =========================================
    // 輔助方法：查詢單一員工權限並填入 DTO
    // =========================================
    private void populatePermissions(EmployeeDTO dto, Integer employeeId) {
        if (dto == null || employeeId == null) {
            return;
        }
        List<EmployeePermission> epList = employeePermissionRepository.findByEmployeeId(employeeId);
        List<Integer> permIds = epList.stream().map(EmployeePermission::getPermissionId).collect(Collectors.toList());
        dto.setPermissionIds(permIds);

        if (!permIds.isEmpty()) {
            List<Permission> perms = permissionRepository.findAllById(permIds);
            List<String> permNames = perms.stream().map(Permission::getPermissionName).collect(Collectors.toList());
            dto.setPermissionNames(permNames);
        } else {
            dto.setPermissionNames(new ArrayList<>());
        }
    }

    // =========================================
    // 輔助方法：獲取或自動建立部門
    // =========================================
    private Department resolveOrCreateDepartment(Integer departmentId, String departmentName) {
        if (departmentId != null) {
            Department dept = departmentRepository.findById(departmentId).orElse(null);
            if (dept != null) {
                return dept;
            }
        }

        if (departmentName != null && !departmentName.isBlank()) {
            String cleanName = departmentName.trim();
            return departmentRepository.findByDepartmentName(cleanName)
                    .orElseGet(() -> {
                        Department newDept = new Department();
                        newDept.setDepartmentName(cleanName);
                        return departmentRepository.save(newDept);
                    });
        }

        return null;
    }

    // =========================================
    // 輔助方法：Entity -> DTO 轉換
    // =========================================
    private EmployeeDTO toDTO(Employee employee, Account account, Profile profile, Department department) {
        EmployeeDTO dto = new EmployeeDTO();
        if (employee != null) {
            dto.setEmployeeId(employee.getEmployeeId());
            dto.setDepartmentId(employee.getDepartmentId());
            dto.setPosition(employee.getPosition());
            dto.setAccountId(employee.getAccountId());
        }
        if (department != null) {
            dto.setDepartmentName(department.getDepartmentName());
        }
        if (account != null) {
            dto.setUsername(account.getUsername());
            dto.setStatus(account.getStatus());
            // 密碼不回傳給前端
        }
        if (profile != null) {
            dto.setProfileId(profile.getProfileId());
            dto.setName(profile.getName());
            dto.setEmail(profile.getEmail());
            dto.setPhone(profile.getPhone());
            dto.setZipcode(profile.getZipcode());
            dto.setCity(profile.getCity());
            dto.setDistrict(profile.getDistrict());
            dto.setAddress(profile.getAddress());
            dto.setCreatedAt(profile.getCreatedAt());
            dto.setBirthday(profile.getBirthday());
            dto.setGender(profile.getGender());
            dto.setUpdatedAt(profile.getUpdatedAt());
        }
        return dto;
    }
}


