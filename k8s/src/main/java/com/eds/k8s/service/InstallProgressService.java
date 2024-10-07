package com.eds.k8s.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eds.k8s.model.InstallLogs;
import com.eds.k8s.model.InstallProgress;
import com.eds.k8s.model.Server;
import com.eds.k8s.repository.InstallLogRepository;
import com.eds.k8s.repository.InstallProgressRepository;

@Service
public class InstallProgressService {

	@Autowired
	private InstallProgressRepository installProgressRepository;

	@Autowired
	private InstallLogRepository installLogRepository;

	public InstallProgress getProgressByServerId(int serverId) {
		return installProgressRepository.findByServerId(serverId)
				.orElseThrow(() -> new IllegalArgumentException("서버에 대한 진행 상태를 찾을 수 없습니다."));
	}

	public void updateProgress(int serverId, int percent, String statusString) {
		InstallProgress progress = installProgressRepository.findByServerId(serverId)
				.orElseThrow(() -> new IllegalArgumentException("서버에 대한 진행 상태를 찾을 수 없습니다."));
		progress.setProgressPercent(percent);

		// String을 InstallProgress.Status로 변환
		InstallProgress.Status status = InstallProgress.Status.valueOf(statusString.toUpperCase());
		progress.setStatus(status); // 변환된 enum을 전달

		installProgressRepository.save(progress);
	}

	public void confiemHost(boolean status, String step) {

	}

	private void updateProgress(Server server, int percent, String logMessage, InstallProgress.Status status,
			int logsTypeId) {
		// Install Progress 업데이트
		Optional<InstallProgress> progressOptional = installProgressRepository.findByServerId(server.getId());
		InstallProgress progress = progressOptional
				.orElse(new InstallProgress(server, 0, InstallProgress.Status.IN_PROGRESS));

		progress.setProgressPercent(percent);
		progress.setStatus(status);
		installProgressRepository.save(progress);

		// 로그 기록
		Optional<InstallLogs> existingLogs = installLogRepository.findByServerIdAndLogsTypeId(server.getId(),
				logsTypeId);
		InstallLogs log;

		if (existingLogs.isPresent()) {
			log = existingLogs.get();
			log.setLogMessage(logMessage);
			log.setLogType(InstallLogs.LogType.INFO);
		} else {
			log = new InstallLogs(server, logMessage, InstallLogs.LogType.INFO, logsTypeId);
		}
		installLogRepository.save(log);
	}
}