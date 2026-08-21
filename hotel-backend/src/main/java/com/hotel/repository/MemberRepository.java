package com.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findByAccountId(Integer accountId);

    void deleteByAccountId(Integer accountId);
}
