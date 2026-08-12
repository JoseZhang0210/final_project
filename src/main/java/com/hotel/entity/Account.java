package com.hotel.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
public class Account {

	@Id
	@Column(name = "account_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "username", nullable = false, length = 50, unique = true)
	private String username;

	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@Column(name = "status", nullable = false, length = 20)
	private String status;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Setter(AccessLevel.NONE)
	private Member member;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Setter(AccessLevel.NONE)
	private Employee employee;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Setter(AccessLevel.NONE)
	private Profile profile;

	public void setMemberRelation(Member member) {
		if (this.member == member)
			return;
		if (this.member != null) {
			Member old = this.member;
			this.member = null;
			old.setAccount(null);
		}
		if (member != null) {
			this.member = member;
			if (member.getAccount() != this) {
				member.setAccount(this);
			}
		}
	}

	public void setEmployeeRelation(Employee employee) {
		if (this.employee == employee)
			return;
		if (this.employee != null) {
			Employee old = this.employee;
			this.employee = null;
			old.setAccount(null);
		}
		if (employee != null) {
			this.employee = employee;
			if (employee.getAccount() != this) {
				employee.setAccount(this);
			}
		}
	}

	public void setProfileRelation(Profile profile) {
		if (this.profile == profile)
			return;
		if (this.profile != null) {
			Profile old = this.profile;
			this.profile = null;
			old.setAccount(null);
		}
		if (profile != null) {
			this.profile = profile;
			if (profile.getAccount() != this) {
				profile.setAccount(this);
			}
		}
	}

	public Account(String username, String password, String status) {
		this.username = username;
		this.password = password;
		this.status = status;
	}
}
