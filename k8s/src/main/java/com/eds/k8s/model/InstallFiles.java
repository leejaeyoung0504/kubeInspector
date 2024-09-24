package com.eds.k8s.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "install_files")
public class InstallFiles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "server_id", nullable = false)
	private Server server;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "file_type_id", nullable = false)
	private FileTypes fileType; // file_types 테이블과 매핑
	
	@Column(name = "file_name", nullable = false)
	private String fileName;

	@Column(name = "file_path", nullable = false)
	private String filePath;

	@Column(name = "uploaded_at", nullable = false, updatable = false, insertable = false)
	private Timestamp uploadedAt;

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public FileTypes getFileType() {
		return fileType;
	}

	public void setFileType(FileTypes fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Timestamp getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(Timestamp uploadedAt) {
		this.uploadedAt = uploadedAt;
	}
}