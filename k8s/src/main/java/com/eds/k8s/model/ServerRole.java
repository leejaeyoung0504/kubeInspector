package com.eds.k8s.model;

import jakarta.persistence.*;

@Entity
@Table(name = "server_roles")
public class ServerRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    // 기본 생성자
    public ServerRole() {}

    // 모든 필드를 포함한 생성자
    public ServerRole(String roleName) {
        this.roleName = roleName;
    }

    // Getter와 Setter methods

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
