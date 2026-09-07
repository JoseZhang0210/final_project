package com.hotel.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

import com.hotel.model.dto.MemberDTO;
import com.hotel.model.entity.Account;
import com.hotel.model.entity.Profile;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.ProfileRepository;
import com.hotel.service.MemberService;
import com.hotel.util.JwtUtils;
import com.hotel.util.MailUtil;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final ProfileRepository profileRepository;
    private final MemberService memberService;
    private final MailUtil mailUtil;

    // 儲存 Email 與驗證碼資訊 (驗證碼、過期時間)
    private static class VerificationCodeRecord {
        final String code;
        final long expireTime;

        VerificationCodeRecord(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    private final Map<String, VerificationCodeRecord> verificationCodes = new ConcurrentHashMap<>();

    // =====================================================
    // 發送信箱驗證碼
    // POST /api/auth/send-code
    // =====================================================
    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            return ResponseEntity.badRequest().body(Map.of("message", "請輸入有效的電子信箱"));
        }
        email = email.trim().toLowerCase();

        // 產生 6 位數隨機數字
        int randomCode = 100000 + (int) (Math.random() * 900000);
        String code = String.valueOf(randomCode);

        // 有效期 5 分鐘
        long expireTime = System.currentTimeMillis() + (5 * 60 * 1000);
        verificationCodes.put(email, new VerificationCodeRecord(code, expireTime));

        try {
            mailUtil.sendVerificationCode(email, code);
            return ResponseEntity.ok(Map.of("message", "驗證碼已發送至您的信箱，請於 5 分鐘內輸入"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "發送驗證碼失敗，請稍後再試"));
        }
    }

    // =====================================================
    // 會員註冊 (含個人資料與信箱驗證碼校驗)
    // POST /api/auth/register
    // =====================================================
    @PostMapping("/register")
    public ResponseEntity<?> registerMember(@RequestBody MemberDTO memberDTO) {
        try {
            // 基本欄位校驗
            if (memberDTO.getUsername() == null || memberDTO.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "請輸入帳號"));
            }
            if (memberDTO.getPassword() == null || memberDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "請輸入密碼"));
            }
            if (memberDTO.getEmail() == null || memberDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "請輸入電子信箱"));
            }

            // 信箱驗證碼校驗
            String email = memberDTO.getEmail().trim().toLowerCase();
            String inputCode = memberDTO.getVerificationCode();
            if (inputCode == null || inputCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "請輸入信箱驗證碼"));
            }

            VerificationCodeRecord record = verificationCodes.get(email);
            if (record == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "尚未向該信箱發送驗證碼，請點擊「發送驗證碼」"));
            }
            if (record.isExpired()) {
                verificationCodes.remove(email);
                return ResponseEntity.badRequest().body(Map.of("message", "驗證碼已過期，請重新發送"));
            }
            if (!record.code.equalsIgnoreCase(inputCode.trim())) {
                return ResponseEntity.badRequest().body(Map.of("message", "驗證碼不正確，請重新確認"));
            }

            // 驗證碼正確，移除快取以防重複使用
            verificationCodes.remove(email);

            // 預設狀態設為啟用 "1"
            memberDTO.setStatus("1");

            // 建立完整會員資料 (Account + Member + Profile)
            MemberDTO createdMember = memberService.createMember(memberDTO);
            return ResponseEntity.ok(createdMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "此帳號已被註冊"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "註冊失敗：" + e.getMessage()));
        }
    }

    // =====================================================
    // 登入
    // POST /api/auth/login
    // =====================================================
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Account account) {
        UserDetails user = userDetailsService.loadUserByUsername(account.getUsername());

        if (!passwordEncoder.matches(account.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Authentication fails because of incorrect password.");
        }

        if (!user.isEnabled()) {
            throw new BadCredentialsException("Account is disabled.");
        }

        String token = jwtUtils.generateToken(user);

        java.util.List<String> authorities = user.getAuthorities().stream()
                .map(auth -> auth != null ? auth.getAuthority() : null)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        String name = profileRepository.findByUsername(user.getUsername())
                .map(Profile::getName)
                .orElse(user.getUsername());

        response.put("token", token);
        response.put("authorities", authorities);
        response.put("name", name);
        return ResponseEntity.ok(response);
    }
}
