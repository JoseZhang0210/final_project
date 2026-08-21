package com.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    Optional<Profile> findByAccountId(Integer accountId);

    void deleteByAccountId(Integer accountId);
}
