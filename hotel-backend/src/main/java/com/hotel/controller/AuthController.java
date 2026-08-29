package com.hotel.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Account;
import com.hotel.model.entity.Profile;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.ProfileRepository;
import com.hotel.util.JwtUtils; // 確保有匯入您的 JwtUtils

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final UserDetailsService userDetailsService; // 1. 注入 Spring Security 的 UserDetailsService
    private final JwtUtils jwtUtils; // 2. 注入您的 JWT 工具類別
    private final ProfileRepository profileRepository;

    @PostMapping("/register")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        try {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            account.setStatus("1");
            Account savedAccount = accountRepository.save(account);
            return ResponseEntity.ok(savedAccount);
        } catch (DataIntegrityViolationException e) {
            // Check if it's a duplicate username constraint violation
            if (e.getMessage() != null && e.getMessage().contains("username")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            // Re-throw if it's a different constraint violation
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Account account) {
        // 3. 修正變數名稱：將 request 改為 account。透過注入的執行個體呼叫方法
        UserDetails user = userDetailsService.loadUserByUsername(account.getUsername());

        // 4. 比對密碼（前端帶來的明文 vs 資料庫的加密值）
        if (!passwordEncoder.matches(account.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Authentication fails because of incorrect password.");
        }

        // Check if account is enabled before issuing JWT
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Account is disabled.");
        }

        // 5. 密碼正確，核發真實的 JWT Token
        String token = jwtUtils.generateToken(user);

        // 順便把 user 裡面的權限清單轉成前端看得懂的字串陣列 [ "ROLE_EMPLOYEE", "order:write" ]
        java.util.List<String> authorities = user.getAuthorities().stream()
                .map(auth -> auth != null ? auth.getAuthority() : null)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

        // 6. 包裝成 JSON 格式回傳給前端（標準做法是回傳帶有 token 欄位的物件）
        Map<String, Object> response = new HashMap<>();
        // 查詢 Profile table 中的使用者姓名
        String name = profileRepository.findByUsername(user.getUsername()).orElse(null).getName();
        
        response.put("token", token);
        response.put("authorities", authorities);
        response.put("name", name);
        return ResponseEntity.ok(response);
    }
}
