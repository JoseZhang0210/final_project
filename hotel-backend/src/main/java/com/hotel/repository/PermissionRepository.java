package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    
}

