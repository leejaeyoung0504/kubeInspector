package com.eds.k8s.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	// 기본 생성자
	public User() {
	}

	// 모든 필드를 포함한 생성자
	public User(String email, String password, String role, Integer userId) {
		this.email = email;
		this.password = password;
		this.role = role;
		this.userId = userId;
	}

	// Getter와 Setter

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getUserId() {
	    return userId;
	}

	public void setUserId(Integer userId) {
	    this.userId = userId;
	}
}