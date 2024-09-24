package com.eds.k8s.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "logs_types")
public class LogsTypes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "logs_types", nullable = false, unique = true)
	private String logs_types;

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogsType() {
		return logs_types;
	}

	public void setLogsType(String logs_types) {
		this.logs_types = logs_types;
	}
}