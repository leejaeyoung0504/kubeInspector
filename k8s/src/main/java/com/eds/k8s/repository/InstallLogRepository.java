package com.eds.k8s.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eds.k8s.model.InstallLogs;

public interface InstallLogRepository extends JpaRepository<InstallLogs, Integer> {
	// 필요한 경우 추가적인 메소드를 정의할 수 있습니다.
	List<InstallLogs> findByServerId(Integer serverId);
	Optional<InstallLogs> findByServerIdAndLogsTypeId(Integer serverId, int logsTypeId);
}
