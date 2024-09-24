package com.eds.k8s.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eds.k8s.model.FileTypes;
import com.eds.k8s.model.InstallFiles;
import com.eds.k8s.model.Server;
import com.eds.k8s.repository.FileTypeRepository;
import com.eds.k8s.repository.InstallFilesRepository;
import com.eds.k8s.repository.ServerRepository;

@Service
public class FileControlService {

	@Autowired
	private InstallFilesRepository installFilesRepository;
	
	@Autowired
	private FileTypeRepository fileTypesRepository;
	
	@Autowired
	private ServerRepository serverRepository;

	public int searchFiles(Server server, String nasDirectory, List<String> files) {
		int fileCount = 0;
		for (String file : files) {
			fileCount ++;
		}
		return fileCount-2;
	}
	// 파일 정보를 DB에 저장
	public void saveInstallFiles(Server server, String nasDirectory, List<String> files) {
		for (String file : files) {
			FileTypes fileType = getFileType(file); // fileTypeId 대신 FileTypes 객체를 가져옴
			if (fileType != null) {
				Optional<InstallFiles> existingFile = installFilesRepository.findByServerAndFileType(server, fileType);
				InstallFiles installFile;
				if(existingFile.isPresent()) {
					installFile = existingFile.get();
					installFile.setServer(server); // server 객체를 설정
					installFile.setFileName(file);
					installFile.setFileType(fileType); // FileTypes 객체를 설정
					installFile.setFilePath(nasDirectory + "/" + file);
				}else {
					installFile = new InstallFiles();
					installFile.setServer(server); // server 객체를 설정
					installFile.setFileName(file);
					installFile.setFileType(fileType); // FileTypes 객체를 설정
					installFile.setFilePath(nasDirectory + "/" + file);
				}
				installFilesRepository.save(installFile);
			}
		}
	}

	// FileTypes 객체를 가져오는 메서드
	private FileTypes getFileType(String fileName) {
		// fileName을 기반으로 적절한 FileTypes 객체를 반환
		// 예시: fileName을 기준으로 적절한 파일 타입을 찾는 로직
		if (fileName.contains("cni")) {
			return fileTypesRepository.findByFileType("CNI");
		} else if (fileName.contains("containerd") && fileName.contains("linux")) {
			return fileTypesRepository.findByFileType("CONTAINERD");
		} else if (fileName.contains("runc")) {
			return fileTypesRepository.findByFileType("RUNC");
		} else if (fileName.equals("kubeadm")) {
			return fileTypesRepository.findByFileType("KUBEADM");
		} else if (fileName.equals("kubectl")) {
			return fileTypesRepository.findByFileType("KUBECTL");
		} else if (fileName.equals("kubelet")) {
			return fileTypesRepository.findByFileType("KUBELET");
		} else if (fileName.contains("tigera")) {
			return fileTypesRepository.findByFileType("TIGERACRI");
		} else if (fileName.contains("custom")) {
			return fileTypesRepository.findByFileType("CUSTOMCRI");
		} else if (fileName.contains("service")) {
			return fileTypesRepository.findByFileType("SERVICE");
		} else if (fileName.contains("conf")) {
			return fileTypesRepository.findByFileType("CONF");
		}
		return null; // 해당 파일 타입을 찾지 못하면 null 반환
	}
	
}
