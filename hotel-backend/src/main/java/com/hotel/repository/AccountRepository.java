package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
	Account findByUsername(String username);
}
