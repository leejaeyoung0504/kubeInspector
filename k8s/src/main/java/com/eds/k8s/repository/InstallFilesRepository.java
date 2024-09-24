package com.eds.k8s.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.eds.k8s.model.FileTypes;
import com.eds.k8s.model.InstallFiles;
import com.eds.k8s.model.Server;

public interface InstallFilesRepository extends JpaRepository<InstallFiles, Integer> {

	// 중복된 파일명 중에서 최신 레코드를 제외한 나머지를 삭제하는 쿼리
    @Modifying
    @Query(value = "DELETE FROM install_files WHERE id NOT IN (SELECT MIN(id) FROM install_files GROUP BY file_name)", nativeQuery = true)
    void deleteDuplicateFileNames();

    Optional<InstallFiles> findByServerAndFileType(Server server, FileTypes fileType);
    List<InstallFiles> findByServer(Server server);

	Optional<InstallFiles> findByFileType(FileTypes fileType);

	Optional<InstallFiles> findByFileTypeId(FileTypes fileType);
}