package com.eds.k8s.service;

import org.springframework.stereotype.Service;

import com.eds.k8s.model.InstallationStatus;
import com.eds.k8s.model.User;
import com.eds.k8s.repository.InstallationStatusRepository;

@Service
public class InstallationStatusService {

    private final InstallationStatusRepository installationStatusRepository;

    public InstallationStatusService(InstallationStatusRepository installationStatusRepository) {
        this.installationStatusRepository = installationStatusRepository;
    }

    public InstallationStatus findByUser(User user) {
        return installationStatusRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("설치 상태 정보를 찾을 수 없습니다."));
    }

    public void updateStepId(InstallationStatus status, int stepId) {
        status.setStepId(stepId);
        installationStatusRepository.save(status);
    }
}
