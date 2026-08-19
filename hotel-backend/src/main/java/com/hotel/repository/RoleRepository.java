package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
