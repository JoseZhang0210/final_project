package com.hotel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Member")
@Data
@NoArgsConstructor
public class Member {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "role_id")
	private Integer roleId;
	
	@Column(name = "status")
	private String status;

	public Member(String name, String email, String password, Integer roleId, String status) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.roleId = roleId;
		this.status = status;
	}
	
	
}
