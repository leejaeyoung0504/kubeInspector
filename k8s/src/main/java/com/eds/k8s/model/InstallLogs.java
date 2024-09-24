package com.eds.k8s.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "install_logs")
public class InstallLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "server_id", nullable = false)
	private Server server;

	@Column(name = "log_message", nullable = false)
	private String logMessage;

	@Enumerated(EnumType.STRING)
	@Column(name = "log_type")
	private LogType logType;
	
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "logs_type_id", nullable = false)
	@Column(name = "logs_type_id", nullable = false)
	private int logsTypeId; // file_types 테이블과 매핑

	@Column(name = "created_at", nullable = false, updatable = false, insertable = false)
	private Timestamp createdAt;

	public enum LogType {
		INFO, WARNING, ERROR
	}

	// 기본 생성자
	public InstallLogs() {
	}

	// 필요한 필드를 받는 생성자
	public InstallLogs(Server server, String logMessage, LogType logType, int logsTypeId) {
		this.server = server;
		this.logMessage = logMessage;
		this.logType = logType;
		this.logsTypeId = logsTypeId;
	}

	// getter/setter 메소드들
	public String getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public LogType getLogType() {
		return logType;
	}

	public void setLogType(LogType logType) {
		this.logType = logType;
	}
	
	public int getLogsTypeId() {
		return logsTypeId;
	}

	public void setLogsTypeId(int logsTypeId) {
		this.logsTypeId = logsTypeId;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}
}