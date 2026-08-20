package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.model.entity.Account;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByUsername (String username);

    // 1. 檢查這個帳號是不是員工，如果是，查出他的職位 (Position)
    @Query(value = "SELECT position FROM employee WHERE account_id = :accountId", nativeQuery = true)
    String findEmployeePosition(@Param("accountId") Integer accountId);

    // 2. 檢查這個帳號是不是會員
    @Query(value = "SELECT COUNT(1) FROM member WHERE account_id = :accountId", nativeQuery = true)
    int checkIsMember(@Param("accountId") Integer accountId);

    // 3. 透過帳號 ID，跨表撈出該員工擁有的所有權限代碼 (Permission Code)
    @Query(value = "SELECT p.permission_code FROM permission p " +
                   "JOIN employee_permission ep ON p.permission_id = ep.permission_id " +
                   "JOIN employee e ON ep.employee_id = e.employee_id " +
                   "WHERE e.account_id = :accountId", nativeQuery = true)
    List<String> findPermissionCodesByAccountId(@Param("accountId") Integer accountId);
}
