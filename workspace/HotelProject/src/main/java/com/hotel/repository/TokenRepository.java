package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Integer> {

}
