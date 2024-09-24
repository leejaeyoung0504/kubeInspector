package com.eds.k8s.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eds.k8s.model.ServerRole;

public interface ServerRoleRepository extends JpaRepository<ServerRole, Integer> {

    
}
