package com.eds.k8s.controller.install;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.eds.k8s.config.AESUtil;
import com.eds.k8s.model.FileTypes;
import com.eds.k8s.model.InstallFiles;
import com.eds.k8s.model.InstallLogs;
import com.eds.k8s.model.InstallProgress;
import com.eds.k8s.model.InstallationStatus;
import com.eds.k8s.model.Server;
import com.eds.k8s.model.ServerRole;
import com.eds.k8s.model.User;
import com.eds.k8s.repository.FileTypeRepository;
import com.eds.k8s.repository.InstallFilesRepository;
import com.eds.k8s.repository.InstallLogRepository;
import com.eds.k8s.repository.InstallProgressRepository;
import com.eds.k8s.repository.InstallationStatusRepository;
import com.eds.k8s.repository.ServerRepository;
import com.eds.k8s.repository.ServerRoleRepository;
import com.eds.k8s.repository.UserRepository;
import com.eds.k8s.service.FileControlService;
import com.eds.k8s.service.InstallFileService;
import com.eds.k8s.service.SSHServiceNew;

@Controller
@RequestMapping(path = "/install")
public class InstallController {

	private final UserRepository userRepository;
	private final InstallationStatusRepository installationStatusRepository;
	private final ServerRepository serverRepository;
	private final InstallProgressRepository installProgressRepository;
	private final InstallLogRepository installLogRepository;
	private final InstallFilesRepository installFilesRepository;

	private final SSHServiceNew sshService;

	private final FileControlService fileControlService;
	private final InstallFileService installFileService;
	private final ServerRoleRepository serverRoleRepository;
	private final FileTypeRepository fileTypeRepository;

	public InstallController(UserRepository userRepository, InstallationStatusRepository installationStatusRepository,
			ServerRepository serverRepository, InstallProgressRepository installProgressRepository,
			InstallLogRepository installLogRepository, InstallFilesRepository installFilesRepository,
			FileControlService fileControlService, InstallFileService installFileService,
			ServerRoleRepository serverRoleRepository, FileTypeRepository fileTypeRepository,
			SSHServiceNew sshService) {
		this.userRepository = userRepository;
		this.installationStatusRepository = installationStatusRepository;
		this.serverRepository = serverRepository;
		this.installProgressRepository = installProgressRepository;
		this.installLogRepository = installLogRepository;
		this.installFilesRepository = installFilesRepository;
		this.fileControlService = fileControlService;
		this.installFileService = installFileService;
		this.serverRoleRepository = serverRoleRepository;
		this.fileTypeRepository = fileTypeRepository;
		this.sshService = sshService;
	}

	String joinCommand;

	@PostMapping(path = "/getStarted")
	public String getIndex(@RequestParam("clusterName") String clusterName, @RequestParam("stepId") int stepId,
			Model model, Authentication authentication // 현재 로그인한 사용자 정보 가져오기
	) {

		// 현재 로그인한 사용자의 이메일 가져오기
		String email = authentication.getName();
		System.out.println("Authenticating user: " + email);

		// 사용자 정보를 UserRepository에서 가져옴
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isEmpty()) {
			return "redirect:/login"; // 사용자가 없으면 로그인 페이지로 리다이렉트
		}

		User user = userOptional.get();
		System.out.println("User found: " + user.getEmail() + ", User ID: " + user.getUserId());

		// 사용자의 installation_status 정보 가져오기
		Optional<InstallationStatus> statusOptional = installationStatusRepository.findByUser(user);
		if (statusOptional.isEmpty()) {
			return "redirect:/installation/start"; // 설치 상태가 없으면 설치 시작 페이지로 리다이렉트
		}

		InstallationStatus installationStatus = statusOptional.get();

		// cluster_name과 step_id 업데이트
		installationStatus.setClusterName(clusterName);
		installationStatus.setStepId(stepId + 1);

		// DB 업데이트
		installationStatusRepository.save(installationStatus);
		// 모델에 값을 추가하여 JSP 페이지로 전달
		model.addAttribute("clusterName", clusterName);
		model.addAttribute("stepId", stepId);

		// 예시: 설치 과정의 다음 단계로 리디렉트
		return "redirect:/main";
	}

	@PostMapping(path = "/installOptionsBack")
	public String installOptionBack(@RequestParam("stepId") int stepId, @RequestParam("actionType") String actionType,
			Model model, Authentication authentication) {
		// 현재 로그인한 사용자의 이메일 가져오기
		String email = authentication.getName();

		// 사용자 정보를 UserRepository에서 가져옴
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isEmpty()) {
			return "redirect:/login"; // 사용자가 없으면 로그인 페이지로 리다이렉트
		}

		User user = userOptional.get();

		// 사용자의 installation_status 정보 가져오기
		Optional<InstallationStatus> statusOptional = installationStatusRepository.findByUser(user);
		if (statusOptional.isEmpty()) {
			return "redirect:/installation/start"; // 설치 상태가 없으면 설치 시작 페이지로 리다이렉트
		}

		InstallationStatus installationStatus = statusOptional.get();

		installationStatus.setStepId(stepId - 1);

		// DB 업데이트
		installationStatusRepository.save(installationStatus);
		if (actionType.equals("confiemHosts")) {
			installLogRepository.deleteAll();
			installProgressRepository.deleteAll();
			installFilesRepository.deleteAll();
		}

		// 모델에 값을 추가하여 JSP 페이지로 전달
		model.addAttribute("stepId", stepId);

		// 예시: 설치 과정의 다음 단계로 리디렉트
		return "redirect:/main";
	}

	@PostMapping(path = "/installOptionsNext")
	@Transactional
	public String installOptionNext(@RequestParam("stepId") int stepId, @RequestParam("hostnames") String hostnames,
			@RequestParam("sshKey") String sshKey, @RequestParam("sshPort") int sshPort,
			@RequestParam("sshUser") String sshUser, @RequestParam("nasDirectory") String nasDirectory, Model model,
			Authentication authentication) {

		// 현재 로그인한 사용자의 이메일로 user_id 찾기
		String email = authentication.getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

		try {
			// SSH 키 암호화
			String secretKey = "1234567890123456"; // 16바이트 길이의 비밀키 (AES 128비트)
			System.out.println("암호화 전 SSH 키: " + sshKey);
			String encryptedSshKey = AESUtil.encrypt(sshKey, secretKey);
			System.out.println("암호화 후 SSH 키: " + encryptedSshKey);

			// 서버 정보를 처리 및 DB에 저장
			String[] hostnameEntries = hostnames.split("\n");

			serverRepository.deleteAll();
			for (String entry : hostnameEntries) {
				String[] parts = entry.split(":");
				String hostname = parts[0].trim();
				String ipAddress = parts[1].trim();

				Optional<Server> existingServer = serverRepository.findByUserIdAndHostname(user.getUserId(), hostname);

				Server server;

				if (existingServer.isPresent()) {
					// 이미 존재하면 업데이트
					server = existingServer.get();
					server.setHostname(hostname);
					server.setSshPort(sshPort);
					server.setSshUser(sshUser);
					server.setSshKey(encryptedSshKey);
					server.setNasDirectory(nasDirectory);
					server.setIpAddress(ipAddress);
					server.setStatus(Server.Status.PENDING); // 상태를 PENDING으로 설정
				} else {
					// 새로운 서버라면 삽입
					server = new Server();
					server.setUser(user);
					server.setHostname(hostname);
					server.setIpAddress(ipAddress);
					server.setSshPort(sshPort);
					server.setSshUser(sshUser);
					server.setSshKey(encryptedSshKey);
					server.setNasDirectory(nasDirectory);
					server.setStatus(Server.Status.PENDING); // 상태를 PENDING으로 설정
				}

				// 서버 저장

				serverRepository.save(server);
			}

			// installation_status 테이블의 step_id 업데이트
			System.out.print("jytest >> +user.getUserId() >>" + user.getId().intValue());
			InstallationStatus status = installationStatusRepository.findByUserId(user.getId().intValue())
					.orElseThrow(() -> new IllegalArgumentException("설치 상태 정보를 찾을 수 없습니다."));
			status.setStepId(stepId + 1); // step_id 업데이트
			installationStatusRepository.save(status);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("SSH 키 암호화 중 오류 발생");
		}

		return "redirect:/main"; // 다음 단계로 리다이렉트
	}

	@PostMapping("/checkInstallProgress")
	@Transactional
	public ModelAndView installProgress(@RequestParam("serverId") List<Integer> serverIds, Model model)
			throws Exception {

		// 복호화
		String secretKey = "1234567890123456";

		for (Integer serverId : serverIds) {
			Server server = serverRepository.findById(serverId)
					.orElseThrow(() -> new IllegalArgumentException("서버를 찾을 수 없습니다."));

			// Install Progress 시작 (0%)
			updateProgress(server, 0, "설치 시작: SSH 통신 준비", InstallProgress.Status.IN_PROGRESS, 1);

			try {
				String encryptedSshKey = server.getSshKey();
				String decrypedSshKey = AESUtil.decrypt(encryptedSshKey, secretKey);
				System.out.println("복호화된 SSH 키: " + decrypedSshKey);
				// SSH 연결 시도
				boolean sshConnected = sshService.connect(server.getIpAddress(), server.getSshPort(),
						server.getSshUser(), decrypedSshKey);
				if (sshConnected) {
					// SSH 통신 성공, 진행 상태 업데이트 (30%)
					updateProgress(server, 30, "SSH 통신 성공", InstallProgress.Status.IN_PROGRESS, 2);

					// Hostname 확인
					boolean hostnameVerified = sshService.verifyHostname(server.getHostname(), server.getIpAddress(),
							server.getSshPort(), server.getSshUser(), decrypedSshKey);
					if (hostnameVerified) {
						// Hostname 확인 성공, 진행 상태 업데이트 (70%)
						updateProgress(server, 70, "Hostname 확인 성공", InstallProgress.Status.IN_PROGRESS, 3);

						// NAS Directory 존재 확인
						boolean nasExists = sshService.checkNasDirectory(server.getNasDirectory(),
								server.getIpAddress(), server.getSshPort(), server.getSshUser(), decrypedSshKey);
						if (nasExists) {
							// NAS 디렉토리 파일 목록 가져오기
							List<String> nasFiles = sshService.getNasFiles(server.getNasDirectory(),
									server.getIpAddress(), server.getSshPort(), server.getSshUser(), decrypedSshKey);
							// boolean returnType = fileControlService.searchFiles(server,
							// server.getNasDirectory(), nasFiles);
							// 파일 정보를 DB에 저장
							fileControlService.saveInstallFiles(server, server.getNasDirectory(), nasFiles);
							// NAS Directory 확인 성공, 진행 상태 업데이트 (100%)
							updateProgress(server, 100, "NAS 디렉토리 확인 성공", InstallProgress.Status.COMPLETED, 4);
							// installFileService.deleteDuplicateFiles();
						} else {
							// NAS Directory 확인 실패
							updateProgress(server, 70, "NAS 디렉토리 확인 실패", InstallProgress.Status.FAILED, 4);
						}
					} else {
						// Hostname 확인 실패
						updateProgress(server, 30, "Hostname 확인 실패", InstallProgress.Status.FAILED, 3);
					}
				} else {
					// SSH 통신 실패
					updateProgress(server, 0, "SSH 통신 실패", InstallProgress.Status.FAILED, 2);
				}
			} catch (Exception e) {
				// 예외 발생 시 로그 기록 및 진행 상태 실패 처리
				updateProgress(server, 0, "서버 통신 중 예외 발생: " + e.getMessage(), InstallProgress.Status.FAILED, 2);
			}
		}

		ModelAndView mav = new ModelAndView();
		mav.setViewName("installPage/confiem-hosts");

		return mav; // JSP 페이지로 리다이렉트
	}

	@GetMapping("/getInstallProgress")
	@ResponseBody
	public Map<String, Object> getInstallProgress(Authentication authentication) {

		String email = authentication.getName();
		System.out.println("Authenticating user: " + email);

		// 사용자 정보를 UserRepository에서 가져옴
		Optional<User> userOptional = userRepository.findByEmail(email);

		User user = userOptional.get();

		System.out.println("User found: " + user.getEmail() + ", User ID: " + user.getUserId());

		// 서버 정보.
		List<Server> servers = serverRepository.findByUserId(user.getUserId());

		List<InstallProgress> progressList = new ArrayList<>();
		List<InstallLogs> logsList = new ArrayList<InstallLogs>();
		// List<>
		for (Server server : servers) {
			Optional<InstallProgress> installProgress = installProgressRepository.findByServerId(server.getId());
			List<InstallLogs> serverLogs = installLogRepository.findByServerId(server.getId());

			// Optional이 값이 있는 경우만 리스트에 추가
			installProgress.ifPresent(progressList::add);
			logsList.addAll(serverLogs); // 해당 서버의 로그를 logsList에 추가
		}

		// 데이터 전달을 위한 맵
		Map<String, Object> response = new HashMap<>();
		response.put("progressList", progressList.isEmpty() ? new ArrayList<>() : progressList);
		response.put("logsList", logsList.isEmpty() ? new ArrayList<>() : logsList);

		return response; // JSON 형식으로 반환
	}

	@PostMapping(path = "/confiemHostNext")
	@Transactional
	public String confiemHostNext(@RequestParam("stepId") int stepId, Model model, Authentication authentication) {

		// 현재 로그인한 사용자의 이메일로 user_id 찾기
		String email = authentication.getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

		try {
			// installation_status 테이블의 step_id 업데이트
			System.out.print("jytest >> +user.getUserId() >>" + user.getId().intValue());
			InstallationStatus status = installationStatusRepository.findByUserId(user.getId().intValue())
					.orElseThrow(() -> new IllegalArgumentException("설치 상태 정보를 찾을 수 없습니다."));
			status.setStepId(stepId + 1); // step_id 업데이트
			installationStatusRepository.save(status);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("SSH 키 암호화 중 오류 발생");
		}

		return "redirect:/main"; // 다음 단계로 리다이렉트
	}

	@PostMapping("/selectVersionNext")
	@Transactional
	public String selectVersion(@RequestParam("stepId") int stepId, @RequestParam("RUNC") String runcFilePath,
			@RequestParam("CRI") String criFilePath, @RequestParam("CONTAINERD") String containerdFilePath,
			@RequestParam("CNI") String cniFilePath, Model model, Authentication authentication) {

		// 선택된 파일 경로들을 처리
		System.out.println("RUNC File Path: " + runcFilePath);
		System.out.println("CRI File Path: " + criFilePath);
		System.out.println("CONTAINERD File Path: " + containerdFilePath);

		String email = authentication.getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

		InstallationStatus status = installationStatusRepository.findByUserId(user.getId().intValue())
				.orElseThrow(() -> new IllegalArgumentException("설치 상태 정보를 찾을 수 없습니다."));
		status.setStepId(stepId + 1); // step_id 업데이트
		installationStatusRepository.save(status);

		return "redirect:/main"; // 다음 단계로 리다이렉트
	}

	@PostMapping("/chooseServiceNext")
	@Transactional
	public String chooseService(@RequestParam("stepId") int stepId, @RequestParam Map<String, String> formData,
			Model model, Authentication authentication) {

		String email = authentication.getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

		// formData에는 server.id 값을 key로 하여 선택된 roleId가 value로 들어 있음
		formData.forEach((serverId, roleId) -> {
			try {
				// stepId 같은 필드 제외
				if (!serverId.equals("stepId")) {
					// 서버와 역할 찾기
					Optional<Server> existingServerOpt = serverRepository.findByUserIdAndId(user.getId().intValue(),
							Integer.parseInt(serverId));
					Optional<ServerRole> existingServerRoleOpt = serverRoleRepository
							.findById(Integer.parseInt(roleId));

					// 서버와 역할이 모두 존재하는지 확인
					if (existingServerOpt.isPresent() && existingServerRoleOpt.isPresent()) {
						Server server = existingServerOpt.get();
						ServerRole serverRole = existingServerRoleOpt.get();

						// 서버의 역할 업데이트
						server.setRole(serverRole);
						serverRepository.save(server); // 서버 저장
						System.out.println("Server ID: " + serverId + " updated with Role ID: " + roleId);
					} else {
						System.out.println(
								"Server or Role not found for Server ID: " + serverId + " and Role ID: " + roleId);
					}
				}
			} catch (Exception e) {
				// 오류 발생 시 로그 출력
				System.err.println("Error updating Server ID: " + serverId + " with Role ID: " + roleId + ". Error: "
						+ e.getMessage());
			}
		});

		InstallationStatus status = installationStatusRepository.findByUserId(user.getId().intValue())
				.orElseThrow(() -> new IllegalArgumentException("설치 상태 정보를 찾을 수 없습니다."));
		status.setStepId(stepId + 1); // step_id 업데이트
		installationStatusRepository.save(status);

		return "redirect:/main"; // 다음 단계로 리다이렉트
	}

	@PostMapping("/installStart")
	@Transactional
	public ResponseEntity<String> installStart(@RequestParam("serverId") List<Integer> serverIds, Model model) {

		String secretKey = "1234567890123456";
		List<String> commands = new ArrayList<>();
		boolean kubeadmInitCompleted = false;
		String hostname = null;
		String decryptedSshKey = null;
		boolean kubeadmInitExecuted = false;
		joinCommand = "";

		for (Integer serverId : serverIds) {
			Server server = findServerById(serverId);

			updateProgress(server, 0, "설치 시작: SSH 통신 준비", InstallProgress.Status.IN_PROGRESS, 1);

			try {
				decryptedSshKey = decryptSshKey(server.getSshKey(), secretKey);

				if (sshService.connect(server.getIpAddress(), server.getSshPort(), server.getSshUser(),
						decryptedSshKey)) {
					updateProgress(server, 30, "SSH 통신 성공", InstallProgress.Status.IN_PROGRESS, 2);

					FileTypes fileTypes = findFileType("CONTAINERD");
					Optional<InstallFiles> installFiles = findInstallFiles(server, fileTypes);

					if (installContainerd(server, decryptedSshKey, installFiles)) {
						fileTypes = findFileType("RUNC");
						installFiles = findInstallFiles(server, fileTypes);
						if (installRunc(server, decryptedSshKey, installFiles)) {
							fileTypes = findFileType("CNI");
							installFiles = findInstallFiles(server, fileTypes);
							if (installCni(server, decryptedSshKey, installFiles)) {
								if (installK8s(server, decryptedSshKey)) {
									if (server.getRole().getId() == 1 && !kubeadmInitExecuted) {
										hostname = server.getHostname();
										kubeadmInitCompleted = executeKubeadmInit(server, decryptedSshKey, hostname,
												commands);
										kubeadmInitExecuted = kubeadmInitCompleted;
									}
								}
							}
						}
					}
				} else {
					updateProgress(server, 0, "SSH 통신 실패", InstallProgress.Status.FAILED, 2);
				}
			} catch (Exception e) {
				handleCommunicationException(server, e);
			}
		}

		if (kubeadmInitCompleted) {
			executeKubeadmJoinOnOtherServers(serverIds, hostname, joinCommand, decryptedSshKey);
		}

		return ResponseEntity.ok("설치 완료");
	}

	private Server findServerById(Integer serverId) {
		return serverRepository.findById(serverId).orElseThrow(() -> new IllegalArgumentException("서버를 찾을 수 없습니다."));
	}

	private FileTypes findFileType(String fileTypeName) {
		return fileTypeRepository.findByFileType(fileTypeName);
	}

	private Optional<InstallFiles> findInstallFiles(Server server, FileTypes fileTypes) {
		return installFilesRepository.findByServerAndFileType(server, fileTypes);
	}

	private String decryptSshKey(String encryptedSshKey, String secretKey) throws Exception {
		return AESUtil.decrypt(encryptedSshKey, secretKey);
	}

	private boolean installContainerd(Server server, String decryptedSshKey, Optional<InstallFiles> installFiles) {

		boolean containerdVerified = sshService.installContainerd(installFiles.get().getFilePath(),
				server.getNasDirectory(), server.getIpAddress(), server.getSshPort(), server.getSshUser(),
				decryptedSshKey);

		if (containerdVerified) {
			updateProgress(server, 10, "Containerd 설치 성공", InstallProgress.Status.IN_PROGRESS, 5);
			return true;
		} else {
			updateProgress(server, 10, "Containerd 설치 실패", InstallProgress.Status.FAILED, 5);
			return false;
		}
	}

	private boolean installRunc(Server server, String decryptedSshKey, Optional<InstallFiles> installFiles) {
		boolean containerdVerified = sshService.installRunc(installFiles.get().getFilePath(), server.getIpAddress(),
				server.getSshPort(), server.getSshUser(), decryptedSshKey);

		if (containerdVerified) {
			updateProgress(server, 20, "Runc 설치 성공", InstallProgress.Status.IN_PROGRESS, 6);
			return true;
		} else {
			updateProgress(server, 20, "Runc 설치 실패", InstallProgress.Status.FAILED, 6);
			return false;
		}
	}

	private boolean installCni(Server server, String decryptedSshKey, Optional<InstallFiles> installFiles) {
		boolean containerdVerified = sshService.installCni(installFiles.get().getFilePath(), server.getIpAddress(),
				server.getSshPort(), server.getSshUser(), decryptedSshKey);

		if (containerdVerified) {
			updateProgress(server, 30, "CNI 설치 성공", InstallProgress.Status.IN_PROGRESS, 7);
			return true;
		} else {
			updateProgress(server, 30, "CNI 설치 실패", InstallProgress.Status.FAILED, 7);
			return false;
		}
	}

	private boolean installK8s(Server server, String decryptedSshKey) {
		boolean containerdVerified = sshService.installK8s(server.getNasDirectory(), server.getIpAddress(),
				server.getSshPort(), server.getSshUser(), decryptedSshKey);

		if (containerdVerified) {
			updateProgress(server, 40, "kubernetes Service 생성 성공", InstallProgress.Status.IN_PROGRESS, 8);
			return true;
		} else {
			updateProgress(server, 40, "kubernetes Service 생성 실패", InstallProgress.Status.FAILED, 8);
			return false;
		}
	}

	private boolean executeKubeadmInit(Server server, String decryptedSshKey, String hostname, List<String> commands) {
		if (!server.getHostname().equals(hostname))
			return false;

		String kubeadmInit = sshService.executeKubeadmInit(server.getIpAddress(), server.getSshPort(),
				server.getSshUser(), decryptedSshKey);

		if ("error".equals(kubeadmInit)) {
			updateProgress(server, 50, "kubeadm init 실행 실패", InstallProgress.Status.IN_PROGRESS, 9);
			return false;
		}

		extractCommands(kubeadmInit, commands);
		return executeKubeadmMkdirCommands(commands, server, decryptedSshKey);
	}

	private boolean executeKubeadmMkdirCommands(List<String> commands, Server server, String decryptedSshKey) {
		for (String command : commands) {
			if (command.contains("kubeadm join")) {
				System.out.println("kubeadm join 명령어는 스킵됩니다: " + command);
				System.out.println("kubeadm join 명령어는 스킵됩니다: " + server.getIpAddress());
				joinCommand = command;
				continue;
			}
			System.out.println("kubeadm join 실행: " + server.getIpAddress());
			boolean result = sshService.executeKubeadmMkdir(command, server.getIpAddress(), server.getSshPort(),
					server.getSshUser(), decryptedSshKey);
			if (!result) {
				updateProgress(server, 50, "kubeadm init 실행 실패", InstallProgress.Status.IN_PROGRESS, 9);
				return false;
			}
		}
		updateProgress(server, 70, "kubeadm init 실행 성공", InstallProgress.Status.IN_PROGRESS, 9);
		return true;
	}

	private void executeKubeadmJoinOnOtherServers(List<Integer> serverIds, String hostname, String joinCommand,
			String decryptedSshKey) {
		for (Integer serverId : serverIds) {
			Server server = findServerById(serverId);

			if (!server.getHostname().equals(hostname)) {
				String result = sshService.executeKubeadmJoin(joinCommand, server.getIpAddress(), server.getSshPort(),
						server.getSshUser(), decryptedSshKey);

				if ("error".equals(result)) {
					updateProgress(server, 70, "kubeadm join 실행 실패", InstallProgress.Status.IN_PROGRESS, 9);
				} else {
					updateProgress(server, 90, "kubeadm join 실행 성공", InstallProgress.Status.IN_PROGRESS, 9);
				}
			}
		}
	}

	private void handleCommunicationException(Server server, Exception e) {
		updateProgress(server, 0, "서버 통신 중 예외 발생: " + e.getMessage(), InstallProgress.Status.FAILED, 2);
	}

	private void extractCommands(String kubeadmInit, List<String> commands) {
		String regex = "(mkdir -p \\$HOME/.kube|sudo cp -i /etc/kubernetes/admin.conf \\$HOME/.kube/config|"
				+ "sudo chown \\$\\(id -u\\):\\$\\(id -g\\) \\$HOME/.kube/config|export KUBECONFIG=/etc/kubernetes/admin.conf|"
				+ "kubeadm join [\\s\\S]+)";
		Matcher matcher = Pattern.compile(regex).matcher(kubeadmInit);

		while (matcher.find()) {
			commands.add(matcher.group());
		}
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
