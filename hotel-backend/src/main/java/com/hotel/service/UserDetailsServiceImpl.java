package com.hotel.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hotel.model.entity.Account;
import com.hotel.repository.AccountRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final AccountRepository accountRepository;

	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("Can't find account: " + username);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Integer accountId = account.getAccountId();

        // 1. 去 employee 表檢查是不是員工
        String position = accountRepository.findEmployeePosition(accountId);
        
        if (position != null) {
            // 💡 員工身分：固定賦予大角色 ROLE_EMPLOYEE (給 Vue 拆 Layout 用)
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));

            // 💡 職位名稱：當作特權標籤塞入 (例如："POSITION_櫃檯")
            authorities.add(new SimpleGrantedAuthority("POSITION_" + position));

            // 跨表撈出該員工在資料庫勾選的細部功能特權（如 order:write）
            List<String> permissionCodes = accountRepository.findPermissionCodesByAccountId(accountId);
            for (String code : permissionCodes) {
                authorities.add(new SimpleGrantedAuthority(code));
            }
        } else {
            // 💡 會員身分：驗證是否為正式會員後才賦予 ROLE_MEMBER
            int memberCount = accountRepository.checkIsMember(accountId);
            if (memberCount > 0) {
                authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
            }
            // If not a verified member, authorities remain empty (least privilege)
        }

        return User.withUsername(username)
                .password(account.getPassword())
                .disabled(!"1".equals(account.getStatus()))
                .authorities(authorities)
                .build();
    }
}