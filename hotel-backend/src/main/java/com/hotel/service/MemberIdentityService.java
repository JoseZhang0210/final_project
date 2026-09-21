package com.hotel.service;

import com.hotel.model.entity.Account;
import com.hotel.model.entity.Member;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberIdentityService {

  private final AccountRepository accountRepository;
  private final MemberRepository memberRepository;

  public Integer requireMemberId(String username) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("無法取得目前登入帳號");
    }

    Account account = accountRepository.findByUsername(username.trim());
    if (account == null) {
      throw new IllegalArgumentException("找不到登入帳號");
    }

    Member member =
        memberRepository
            .findByAccountId(account.getAccountId())
            .orElseThrow(() -> new IllegalArgumentException("目前登入帳號不是會員"));
    return member.getMemberId();
  }
}
