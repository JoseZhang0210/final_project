package com.hotel.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.MemberDTO;
import com.hotel.model.entity.Account;
import com.hotel.model.entity.Member;
import com.hotel.model.entity.Profile;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.ProfileRepository;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================
    // 1. 查詢所有會員（整合 Account 與 Profile，支援關鍵字與狀態篩選）
    // =========================================
    @Transactional(readOnly = true)
    public List<MemberDTO> findAllMembers(String keyword, String status) {
        List<Member> members = memberRepository.findAll();
        List<MemberDTO> list = new ArrayList<>();

        for (Member member : members) {
            Account account = null;
            if (member.getAccountId() != null) {
                account = accountRepository.findById(member.getAccountId()).orElse(null);
            }

            Profile profile = null;
            if (member.getAccountId() != null) {
                profile = profileRepository.findByAccountId(member.getAccountId()).orElse(null);
            }

            MemberDTO dto = toDTO(member, account, profile);
            list.add(dto);
        }

        // 篩選 status
        if (status != null && !status.isBlank()) {
            list = list.stream()
                    .filter(m -> status.equals(m.getStatus()))
                    .collect(Collectors.toList());
        }

        // 搜尋 keyword (比對 username, name, email, phone)
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();
            list = list.stream()
                    .filter(m -> (m.getUsername() != null && m.getUsername().toLowerCase().contains(kw)) ||
                            (m.getName() != null && m.getName().toLowerCase().contains(kw)) ||
                            (m.getEmail() != null && m.getEmail().toLowerCase().contains(kw)) ||
                            (m.getPhone() != null && m.getPhone().contains(kw)))
                    .collect(Collectors.toList());
        }

        return list;
    }

    // =========================================
    // 2. 依 ID 查詢單一會員詳細資料
    // =========================================
    @Transactional(readOnly = true)
    public MemberDTO findById(Integer memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return null;
        }

        Account account = null;
        if (member.getAccountId() != null) {
            account = accountRepository.findById(member.getAccountId()).orElse(null);
        }

        Profile profile = null;
        if (member.getAccountId() != null) {
            profile = profileRepository.findByAccountId(member.getAccountId()).orElse(null);
        }

        return toDTO(member, account, profile);
    }

    // =========================================
    // 3. 新增會員（同步建立 Account, Member, Profile）
    // =========================================
    public MemberDTO createMember(MemberDTO dto) {
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

        // 2. 建立 Member
        Member member = new Member();
        member.setAccountId(savedAccount.getAccountId());
        Member savedMember = memberRepository.save(member);

        // 3. 建立 Profile
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

        return toDTO(savedMember, savedAccount, savedProfile);
    }

    // =========================================
    // 4. 修改會員詳細資料
    // =========================================
    public MemberDTO updateMember(Integer memberId, MemberDTO dto) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return null;
        }

        Account account = null;
        if (member.getAccountId() != null) {
            account = accountRepository.findById(member.getAccountId()).orElse(null);
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

        Profile profile = null;
        if (member.getAccountId() != null) {
            profile = profileRepository.findByAccountId(member.getAccountId()).orElse(null);
        }

        if (profile == null && member.getAccountId() != null) {
            profile = new Profile();
            profile.setAccountId(member.getAccountId());
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

        return toDTO(member, account, profile);
    }

    // =========================================
    // 5. 快速更新會員帳號狀態 (啟用/停用)
    // =========================================
    public MemberDTO updateMemberStatus(Integer memberId, String status) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return null;
        }

        Account account = null;
        if (member.getAccountId() != null) {
            account = accountRepository.findById(member.getAccountId()).orElse(null);
        }

        if (account != null) {
            account.setStatus(status);
            account = accountRepository.save(account);
        }

        Profile profile = null;
        if (member.getAccountId() != null) {
            profile = profileRepository.findByAccountId(member.getAccountId()).orElse(null);
        }

        return toDTO(member, account, profile);
    }

    // =========================================
    // 6. 刪除會員（連動刪除 Profile, Member, Account）
    // =========================================
    public boolean deleteMember(Integer memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return false;
        }

        Integer accountId = member.getAccountId();

        // 1. 刪除 Profile
        if (accountId != null) {
            profileRepository.deleteByAccountId(accountId);
        }

        // 2. 刪除 Member
        memberRepository.delete(member);

        // 3. 刪除 Account
        if (accountId != null) {
            accountRepository.deleteById(accountId);
        }

        return true;
    }

    // =========================================
    // 輔助方法：Entity -> DTO 轉換
    // =========================================
    private MemberDTO toDTO(Member member, Account account, Profile profile) {
        MemberDTO dto = new MemberDTO();
        if (member != null) {
            dto.setMemberId(member.getMemberId());
            dto.setAccountId(member.getAccountId());
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
