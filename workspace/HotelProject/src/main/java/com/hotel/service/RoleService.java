package com.hotel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.Role;
import com.hotel.repository.RoleRepository;

@Service
@Transactional
public class RoleService {
	
	private final RoleRepository roleRepository;
	
	
	
	public RoleService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}



	public Role insert(Role role) {
		return roleRepository.save(role);
	}
}
