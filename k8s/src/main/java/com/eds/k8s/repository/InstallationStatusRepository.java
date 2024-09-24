package com.eds.k8s.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eds.k8s.model.InstallationStatus;
import com.eds.k8s.model.Server;
import com.eds.k8s.model.User;

public interface InstallationStatusRepository extends JpaRepository<InstallationStatus, Long> {
    // user_id로 installation_status를 찾기 위한 메서드
    Optional<InstallationStatus> findByUser(User user);
    Optional<InstallationStatus> findByUserId(Integer userId);
}