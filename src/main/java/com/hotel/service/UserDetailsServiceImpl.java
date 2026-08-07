package com.hotel.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hotel.entity.Account;
import com.hotel.repository.AccountRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final AccountRepository accountRepository;

	UserDetailsServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findByUsername(username);
		if (account == null) {
			throw new UsernameNotFoundException("Can't find account: " + username);
		}

		return User.withUsername(username).password(account.getPassword()).build();
	}
}