package com.eds.k8s.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "servers")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Many servers can belong to one user (ManyToOne 관계)
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "servers_ibfk_1"))
    private User user;

    @Column(name = "hostname", nullable = false, length = 100)
    private String hostname;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "ssh_port", nullable = false)
    private Integer sshPort = 22;  // 기본값 22

    @Column(name = "ssh_user", nullable = false, length = 50)
    private String sshUser;

    @Column(name = "ssh_key", columnDefinition = "TEXT", nullable = false)
    private String sshKey;

    @Column(name = "nas_directory", nullable = false, length = 255)
    private String nasDirectory;

    // Many servers can have one role (ManyToOne 관계)
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "servers_ibfk_2"))
    private ServerRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('PENDING', 'VERIFIED', 'FAILED')", nullable = false)
    private Status status = Status.PENDING;  // 기본값 'PENDING'

    @Column(name = "created_at", nullable = true)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true)
    private Timestamp updatedAt;

    // Enum for status field
    public enum Status {
        PENDING, VERIFIED, FAILED
    }

    // 기본 생성자
    public Server() {}

    // Getter와 Setter methods

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getSshUser() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey;
    }

    public String getNasDirectory() {
        return nasDirectory;
    }

    public void setNasDirectory(String nasDirectory) {
        this.nasDirectory = nasDirectory;
    }

    public ServerRole getRole() {
        return role;
    }

    public void setRole(ServerRole role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}