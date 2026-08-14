package com.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.entity.Account;
import com.hotel.entity.Member;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.MemberRepository;

@RestController
public class AccountController {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;

    AccountController(AccountRepository accountRepository, MemberRepository memberRepository,
            PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/accounts")
    public ResponseEntity<Void> createAccount(@RequestBody RegistrationRequest request) {
        if (request == null || request.username() == null || request.password() == null
                || request.username().isBlank() || request.password().length() < 8) {
            return ResponseEntity.badRequest().build();
        }

        String username = request.username().trim();
        if (username.length() < 3 || username.length() > 50) {
            return ResponseEntity.badRequest().build();
        }
        if (accountRepository.findByUsername(username) != null) {
            return ResponseEntity.status(409).build();
        }

        Account account = new Account(username, passwordEncoder.encode(request.password()), "ACTIVE");
        Account savedAccount = accountRepository.save(account);
        memberRepository.save(new Member(savedAccount));
        return ResponseEntity.status(201).build();
    }

    public record RegistrationRequest(String username, String password) {
    }
}
