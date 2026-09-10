package com.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    List<Permission> findAllByOrderByPermissionIdAsc();

    boolean existsByPermissionCodeIgnoreCase(String permissionCode);

    boolean existsByPermissionNameIgnoreCase(String permissionName);

    Optional<Permission> findByPermissionCodeIgnoreCase(String permissionCode);

    Optional<Permission> findByPermissionNameIgnoreCase(String permissionName);
}

