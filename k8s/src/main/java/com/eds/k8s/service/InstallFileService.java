package com.eds.k8s.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eds.k8s.repository.InstallFilesRepository;

import jakarta.transaction.Transactional;

@Service
public class InstallFileService {

    @Autowired
    private InstallFilesRepository installFilesRepository;

    // 중복된 파일명 삭제 메서드
    @Transactional
    public void deleteDuplicateFiles() {
        installFilesRepository.deleteDuplicateFileNames();
    }
}
