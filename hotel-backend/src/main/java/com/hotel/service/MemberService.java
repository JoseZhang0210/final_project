package com.hotel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.MemberDTO;
import com.hotel.model.dto.MemberDemographicsDTO;
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
    // 7. 透過 username 查詢自己的資料（供 /me 端點使用）
    // =========================================
    @Transactional(readOnly = true)
    public MemberDTO findByUsername(String username) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            return null;
        }

        Member member = memberRepository.findByAccountId(account.getAccountId()).orElse(null);
        Profile profile = profileRepository.findByAccountId(account.getAccountId()).orElse(null);

        return toDTO(member, account, profile);
    }

    // =========================================
    // 8. 透過 username 更新自己的資料（供 /me 端點使用）
    // =========================================
    public MemberDTO updateMemberByUsername(String username, MemberDTO dto) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            return null;
        }

        Member member = memberRepository.findByAccountId(account.getAccountId()).orElse(null);
        if (member == null) {
            return null;
        }

        return updateMember(member.getMemberId(), dto);
    }

    // =========================================
    // 9. 會員修改密碼
    // =========================================
    public void changePassword(String username, String currentPassword, String newPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("使用者帳號無效");
        }
        Account account = accountRepository.findByUsername(username.trim());
        if (account == null) {
            throw new IllegalArgumentException("查無此會員帳號");
        }
        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("請輸入目前的密碼");
        }
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("新密碼長度至少需為 6 個字元");
        }
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new IllegalArgumentException("目前密碼輸入錯誤，請重新確認");
        }
        if (passwordEncoder.matches(newPassword.trim(), account.getPassword())) {
            throw new IllegalArgumentException("新密碼不能與目前密碼相同");
        }
        account.setPassword(passwordEncoder.encode(newPassword.trim()));
        accountRepository.save(account);
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

    // =========================================
    // 10. 會員人口統計分佈（地區、年齡、性別）
    // =========================================
    @Transactional(readOnly = true)
    public MemberDemographicsDTO getMemberDemographics() {
        List<Member> members = memberRepository.findAll();
        long totalMembers = members.size();

        if (totalMembers == 0) {
            return MemberDemographicsDTO.builder()
                    .totalMembers(0L)
                    .profileCount(0L)
                    .profileCompletionRate(0.0)
                    .topCity("無資料")
                    .topCityPercentage(0.0)
                    .topAgeGroup("無資料")
                    .topAgeGroupPercentage(0.0)
                    .cityDistribution(new ArrayList<>())
                    .ageDistribution(new ArrayList<>())
                    .genderDistribution(new ArrayList<>())
                    .build();
        }

        List<Profile> allProfiles = profileRepository.findAll();
        Map<Integer, Profile> profileMap = allProfiles.stream()
                .filter(p -> p.getAccountId() != null)
                .collect(Collectors.toMap(Profile::getAccountId, p -> p, (existing, replacement) -> existing));

        long profileCount = 0;
        Map<String, Long> cityCounts = new HashMap<>();
        long age18to24 = 0;
        long age25to34 = 0;
        long age35to44 = 0;
        long age45to54 = 0;
        long age55plus = 0;
        long ageUnknown = 0;

        long maleCount = 0;
        long femaleCount = 0;
        long otherGenderCount = 0;

        LocalDate today = LocalDate.now();

        for (Member member : members) {
            Profile p = member.getAccountId() != null ? profileMap.get(member.getAccountId()) : null;
            if (p != null) {
                profileCount++;

                // 縣市資料處理與正規化
                String city = p.getCity();
                if (city == null || city.trim().isBlank()) {
                    city = "未填寫";
                } else {
                    city = city.trim();
                    if (city.startsWith("臺")) {
                        city = "台" + city.substring(1);
                    }
                }
                cityCounts.put(city, cityCounts.getOrDefault(city, 0L) + 1);

                // 年齡計算與分組
                if (p.getBirthday() != null) {
                    int age = Period.between(p.getBirthday(), today).getYears();
                    if (age >= 18 && age <= 24) {
                        age18to24++;
                    } else if (age >= 25 && age <= 34) {
                        age25to34++;
                    } else if (age >= 35 && age <= 44) {
                        age35to44++;
                    } else if (age >= 45 && age <= 54) {
                        age45to54++;
                    } else if (age >= 55) {
                        age55plus++;
                    } else {
                        ageUnknown++;
                    }
                } else {
                    ageUnknown++;
                }

                // 性別統計
                String gender = p.getGender();
                if (gender != null) {
                    gender = gender.trim().toUpperCase();
                    if ("M".equals(gender) || "男".equals(gender) || "MALE".equals(gender)) {
                        maleCount++;
                    } else if ("F".equals(gender) || "女".equals(gender) || "FEMALE".equals(gender)) {
                        femaleCount++;
                    } else {
                        otherGenderCount++;
                    }
                } else {
                    otherGenderCount++;
                }
            } else {
                cityCounts.put("未填寫", cityCounts.getOrDefault("未填寫", 0L) + 1);
                ageUnknown++;
                otherGenderCount++;
            }
        }

        double profileCompletionRate = Math.round((double) profileCount / totalMembers * 1000.0) / 10.0;

        // 地區分佈排序
        List<MemberDemographicsDTO.CityStatDTO> cityList = cityCounts.entrySet().stream()
                .map(e -> {
                    double pct = Math.round((double) e.getValue() / totalMembers * 1000.0) / 10.0;
                    return MemberDemographicsDTO.CityStatDTO.builder()
                            .city(e.getKey())
                            .count(e.getValue())
                            .percentage(pct)
                            .build();
                })
                .sorted((a, b) -> {
                    if ("未填寫".equals(a.getCity())) return 1;
                    if ("未填寫".equals(b.getCity())) return -1;
                    return Long.compare(b.getCount(), a.getCount());
                })
                .collect(Collectors.toList());

        String topCity = "無資料";
        double topCityPct = 0.0;
        Optional<MemberDemographicsDTO.CityStatDTO> topCityOpt = cityList.stream()
                .filter(c -> !"未填寫".equals(c.getCity()))
                .findFirst();
        if (topCityOpt.isPresent()) {
            topCity = topCityOpt.get().getCity();
            topCityPct = topCityOpt.get().getPercentage();
        } else if (!cityList.isEmpty()) {
            topCity = cityList.get(0).getCity();
            topCityPct = cityList.get(0).getPercentage();
        }

        // 年齡層分組
        List<MemberDemographicsDTO.AgeGroupStatDTO> ageList = new ArrayList<>();
        ageList.add(createAgeGroupStat("18-24 歲", 18, 24, age18to24, totalMembers));
        ageList.add(createAgeGroupStat("25-34 歲", 25, 34, age25to34, totalMembers));
        ageList.add(createAgeGroupStat("35-44 歲", 35, 44, age35to44, totalMembers));
        ageList.add(createAgeGroupStat("45-54 歲", 45, 54, age45to54, totalMembers));
        ageList.add(createAgeGroupStat("55 歲以上", 55, 120, age55plus, totalMembers));
        ageList.add(createAgeGroupStat("未填寫", null, null, ageUnknown, totalMembers));

        String topAgeGroup = "無資料";
        double topAgeGroupPct = 0.0;
        Optional<MemberDemographicsDTO.AgeGroupStatDTO> topAgeOpt = ageList.stream()
                .filter(a -> !"未填寫".equals(a.getGroupName()))
                .max((a, b) -> Long.compare(a.getCount(), b.getCount()));
        if (topAgeOpt.isPresent() && topAgeOpt.get().getCount() > 0) {
            topAgeGroup = topAgeOpt.get().getGroupName();
            topAgeGroupPct = topAgeOpt.get().getPercentage();
        }

        // 性別比例
        List<MemberDemographicsDTO.GenderStatDTO> genderList = new ArrayList<>();
        genderList.add(MemberDemographicsDTO.GenderStatDTO.builder()
                .gender("M")
                .label("男")
                .count(maleCount)
                .percentage(Math.round((double) maleCount / totalMembers * 1000.0) / 10.0)
                .build());
        genderList.add(MemberDemographicsDTO.GenderStatDTO.builder()
                .gender("F")
                .label("女")
                .count(femaleCount)
                .percentage(Math.round((double) femaleCount / totalMembers * 1000.0) / 10.0)
                .build());
        genderList.add(MemberDemographicsDTO.GenderStatDTO.builder()
                .gender("OTHER")
                .label("其他 / 未填寫")
                .count(otherGenderCount)
                .percentage(Math.round((double) otherGenderCount / totalMembers * 1000.0) / 10.0)
                .build());

        return MemberDemographicsDTO.builder()
                .totalMembers(totalMembers)
                .profileCount(profileCount)
                .profileCompletionRate(profileCompletionRate)
                .topCity(topCity)
                .topCityPercentage(topCityPct)
                .topAgeGroup(topAgeGroup)
                .topAgeGroupPercentage(topAgeGroupPct)
                .cityDistribution(cityList)
                .ageDistribution(ageList)
                .genderDistribution(genderList)
                .build();
    }

    private MemberDemographicsDTO.AgeGroupStatDTO createAgeGroupStat(String groupName, Integer minAge, Integer maxAge, long count, long total) {
        double pct = total > 0 ? Math.round((double) count / total * 1000.0) / 10.0 : 0.0;
        return MemberDemographicsDTO.AgeGroupStatDTO.builder()
                .groupName(groupName)
                .minAge(minAge)
                .maxAge(maxAge)
                .count(count)
                .percentage(pct)
                .build();
    }
}
