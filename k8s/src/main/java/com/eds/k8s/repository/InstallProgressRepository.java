package com.eds.k8s.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.eds.k8s.model.InstallProgress;

public interface InstallProgressRepository extends JpaRepository<InstallProgress, Integer> {
    Optional<InstallProgress> findByServerId(Integer serverId); 
}