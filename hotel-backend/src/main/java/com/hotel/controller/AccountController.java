package com.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.entity.Account;
import com.hotel.repository.AccountRepository;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    AccountController(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setStatus("1");
        Account savedAccount = accountRepository.save(account);
        return ResponseEntity.ok(savedAccount);
    }
}
