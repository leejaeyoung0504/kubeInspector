package com.eds.k8s.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eds.k8s.model.InstallProgress;
import com.eds.k8s.repository.InstallProgressRepository;

@Service
public class InstallProgressService {

	@Autowired
	private InstallProgressRepository installProgressRepository;

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
}