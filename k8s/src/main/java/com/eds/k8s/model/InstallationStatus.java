package com.eds.k8s.model;

import jakarta.persistence.*;

@Entity
public class InstallationStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // User와의 관계 설정

	@Column(nullable = false)
	private Integer stepId; // 설치 단계
	
	@Column(name = "cluster_name", nullable = false)
	private String clusterName; // 설치 단계

	// 생성자, getter, setter 추가
	public InstallationStatus() {
	}

	public InstallationStatus(User user, Integer stepId, String clusterName) {
		this.user = user;
		this.stepId = stepId;
		this.clusterName = clusterName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getStepId() {
		return stepId;
	}

	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}
	
	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
}
