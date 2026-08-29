package com.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.model.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    Optional<Profile> findByAccountId(Integer accountId);

    void deleteByAccountId(Integer accountId);
    
    @Query(value = "SELECT p.* FROM profile p JOIN account a ON p.account_id = a.account_id WHERE a.username = :username", nativeQuery = true)
    Optional<Profile> findByUsername(@Param("username") String username);
}
