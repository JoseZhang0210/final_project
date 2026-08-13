package com.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.entity.Account;
import com.hotel.entity.Member;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.MemberRepository;

@RestController
@RequestMapping("/admin")
public class MemberAdminController {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberAdminController(
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getMembers() {
        List<MemberResponse> members = memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Integer id) {
        return memberRepository.findById(id)
                .map(MemberResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberCreateRequest request) {
        if (request == null || request.username() == null || request.username().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (accountRepository.findByUsername(request.username().trim()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        String normalizedStatus = request.status() == null || request.status().isBlank()
                ? "ACTIVE"
                : request.status().trim();

        String password = request.password() == null || request.password().isBlank()
                ? "123456"
                : request.password();

        Account account = new Account();
        account.setUsername(request.username().trim());
        account.setPassword(passwordEncoder.encode(password));
        account.setStatus(normalizedStatus);

        Account savedAccount = accountRepository.save(account);
        Member member = new Member(savedAccount);
        savedAccount.setMemberRelation(member);
        Member savedMember = memberRepository.save(member);

        return ResponseEntity.status(HttpStatus.CREATED).body(MemberResponse.from(savedMember));
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Integer id,
            @RequestBody MemberUpdateRequest request) {

        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        Member member = memberRepository.findById(id).orElse(null);
        if (member == null || member.getAccount() == null) {
            return ResponseEntity.notFound().build();
        }

        Account account = member.getAccount();

        if (request.username() != null && !request.username().isBlank()) {
            String newUsername = request.username().trim();
            if (!newUsername.equals(account.getUsername())
                    && accountRepository.findByUsername(newUsername) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            account.setUsername(newUsername);
        }

        if (request.status() != null && !request.status().isBlank()) {
            account.setStatus(request.status().trim());
        }

        if (request.password() != null && !request.password().isBlank()) {
            account.setPassword(passwordEncoder.encode(request.password()));
        }

        Account savedAccount = accountRepository.save(account);
        savedAccount.setMemberRelation(member);
        Member savedMember = memberRepository.save(member);

        return ResponseEntity.ok(MemberResponse.from(savedMember));
    }

    @PatchMapping("/members/{id}/status")
    public ResponseEntity<MemberResponse> updateMemberStatus(
            @PathVariable Integer id,
            @RequestBody MemberStatusRequest request) {

        if (request == null || request.status() == null || request.status().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Member member = memberRepository.findById(id).orElse(null);
        if (member == null || member.getAccount() == null) {
            return ResponseEntity.notFound().build();
        }

        Account account = member.getAccount();
        account.setStatus(request.status().trim());

        memberRepository.save(member);
        return ResponseEntity.ok(MemberResponse.from(member));
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Integer id) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) {
            return ResponseEntity.notFound().build();
        }

        Account account = member.getAccount();
        if (account != null) {
            accountRepository.delete(account);
        }
        memberRepository.delete(member);

        return ResponseEntity.noContent().build();
    }

    public record MemberResponse(Integer id, String username, String status) {
        public static MemberResponse from(Member member) {
            Account account = member.getAccount();
            return new MemberResponse(
                    member.getId(),
                    account != null ? account.getUsername() : null,
                    account != null ? account.getStatus() : null);
        }
    }

    public record MemberCreateRequest(String username, String password, String status) {
    }

    public record MemberUpdateRequest(String username, String password, String status) {
    }

    public record MemberStatusRequest(String status) {
    }
}
