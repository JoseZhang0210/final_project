package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
}
