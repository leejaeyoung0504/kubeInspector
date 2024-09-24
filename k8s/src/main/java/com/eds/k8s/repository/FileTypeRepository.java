package com.eds.k8s.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eds.k8s.model.FileTypes;

public interface FileTypeRepository extends JpaRepository<FileTypes, Integer> {

	FileTypes findByFileType(String string);
    // 필요한 경우 추가 쿼리 메서드를 정의할 수 있습니다.
}