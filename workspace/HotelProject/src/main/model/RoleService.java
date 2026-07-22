package com.hotel.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
