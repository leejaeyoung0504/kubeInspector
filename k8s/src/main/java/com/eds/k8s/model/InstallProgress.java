package com.eds.k8s.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "install_progress")
public class InstallProgress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "server_id", nullable = false)
	private Server server;

	private int progressPercent;

	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(name = "updated_at", nullable = false, updatable = false, insertable = false)
	private Timestamp updatedAt;

	public enum Status {
		IN_PROGRESS, COMPLETED, FAILED
	}

	// 기본 생성자
	public InstallProgress() {
	}

	// 필요한 필드를 받는 생성자
	public InstallProgress(Server server, int progressPercent, Status status) {
		this.server = server;
		this.progressPercent = progressPercent;
		this.status = status;
	}

	// getter/setter 메소드들
	public int getProgressPercent() {
		return progressPercent;
	}

	public void setProgressPercent(int progressPercent) {
		this.progressPercent = progressPercent;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}
}