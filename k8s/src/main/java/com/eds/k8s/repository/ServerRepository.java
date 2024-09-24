package com.eds.k8s.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eds.k8s.model.Server;

public interface ServerRepository extends JpaRepository<Server, Integer> {
	List<Server> findByUserId(Integer userId);
	//Optional<Server> findByUserId(Integer userId);
    Optional<Server> findByUserIdAndHostname(Integer userId, String hostname);
    Optional<Server> findByUserIdAndId(Integer userId, Integer id);
    
}
