package com.hotel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Account account = accountRepository.findByUsername(username);

        if (account == null) {
            throw new UsernameNotFoundException(
                    "Can't find account: " + username);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Integer accountId = account.getAccountId();

        String normalizedUsername =
                String.valueOf(account.getUsername())
                        .trim()
                        .toLowerCase(Locale.ROOT);

        boolean adminAccount =
                normalizedUsername.startsWith("admin");

        String position =
                accountRepository.findEmployeePosition(accountId);

        if (position != null || adminAccount) {
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_EMPLOYEE"));

            if (adminAccount) {
                authorities.add(
                        new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

            if (position != null) {
                authorities.add(
                        new SimpleGrantedAuthority(
                                "POSITION_" + position));

                List<String> permissionCodes =
                        accountRepository
                                .findPermissionCodesByAccountId(accountId);

                for (String code : permissionCodes) {
                    if (code != null && !code.isBlank()) {
                        authorities.add(
                                new SimpleGrantedAuthority(code));
                    }
                }
            }
        } else {
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_MEMBER"));
        }

        return User.withUsername(username)
                .password(account.getPassword())
                .disabled(!"1".equals(account.getStatus()))
                .authorities(authorities)
                .build();
    }
}