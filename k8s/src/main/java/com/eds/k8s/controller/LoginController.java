package com.eds.k8s.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.eds.k8s.config.AESUtil;
import com.eds.k8s.model.InstallFiles;
import com.eds.k8s.model.InstallationStatus;
import com.eds.k8s.model.Server;
import com.eds.k8s.model.ServerRole;
import com.eds.k8s.model.User;
import com.eds.k8s.repository.InstallFilesRepository;
import com.eds.k8s.repository.InstallLogRepository;
import com.eds.k8s.repository.InstallProgressRepository;
import com.eds.k8s.repository.InstallationStatusRepository;
import com.eds.k8s.repository.ServerRepository;
import com.eds.k8s.repository.ServerRoleRepository;
import com.eds.k8s.repository.UserRepository;

@Controller
public class LoginController {

	private final UserRepository userRepository;
	private final InstallationStatusRepository installationStatusRepository;
	private final ServerRepository serverRepository;
	private final InstallProgressRepository installProgressRepository;
	private final InstallLogRepository installLogRepository;

	@Autowired
	private InstallFilesRepository installFilesRepository;
	@Autowired
	private ServerRoleRepository serverRoleRepository;

	public LoginController(UserRepository userRepository, InstallationStatusRepository installationStatusRepository,
			ServerRepository serverRepository, InstallProgressRepository installProgressRepository,
			InstallLogRepository installLogRepository) {
		this.userRepository = userRepository;
		this.installationStatusRepository = installationStatusRepository;
		this.serverRepository = serverRepository;
		this.installProgressRepository = installProgressRepository;
		this.installLogRepository = installLogRepository;
	}

	@GetMapping("/login")
	public String login(Authentication authentication) {
		return "login";
	}

	@GetMapping("/main")
	public ModelAndView main(Authentication authentication, Model model) throws JSONException {
		// 현재 로그인한 사용자의 이메일 가져오기
		String email = authentication.getName();

		ModelAndView mav = new ModelAndView();

		// 사용자 정보를 가져옴
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isEmpty()) {
			mav.setViewName("redirect:/login");
		}
		User user = userOptional.get();
		Integer userId = user.getUserId();
		System.out.println("User found: " + user.getEmail() + ", User ID: " + user.getUserId());

		// 사용자의 설치 상태를 가져옴
		Optional<InstallationStatus> statusOptional = installationStatusRepository.findByUser(user);
		if (statusOptional.isEmpty()) {
			// return "redirect:/installation/start"; // 설치 시작 페이지로 리디렉션
		}

		// Optional<Server> serverOptional = serverRepository.findByUserId(userId);
		List<Server> servers = serverRepository.findByUserId(userId);
		// Server server = serverOptional.get();

		// InstallationStatus에서 stepId를 확인
		Integer stepId = statusOptional.get().getStepId();
		String clusterName = statusOptional.get().getClusterName();

		if (stepId == 1) {
			mav.setViewName("installPage/get-started");
			mav.addObject("clustarName", clusterName);
		} else if (stepId == 2) {
			if (!servers.isEmpty()) {
				HashMap<String, String> map = new HashMap<String, String>();
				// 암호화 된 ssh
				String encryptedSshKey = servers.get(0).getSshKey();

				// 복호화
				String secretKey = "1234567890123456";
				try {
					String decrypedSshKey = AESUtil.decrypt(encryptedSshKey, secretKey);
					mav.addObject("sshKey", decrypedSshKey);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("SSH 키 복호화 중 오류 발생");
				}
				for (int i = 0; i < servers.size(); i++) {
					map.put(servers.get(i).getHostname(), servers.get(i).getIpAddress());
				}
				mav.addObject("hostInfo", map);
				mav.addObject("port", servers.get(0).getSshPort());
				mav.addObject("nfsDir", servers.get(0).getNasDirectory());
			}
			mav.setViewName("installPage/install-options");
		} else if (stepId == 3) {
			if (!servers.isEmpty()) {
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				for (int i = 0; i < servers.size(); i++) {
					map.put(servers.get(i).getHostname(), servers.get(i).getId());
				}
				mav.addObject(map);
			}
			mav.setViewName("installPage/confiem-hosts");
		} else if (stepId == 4) {
			mav.setViewName("installPage/select-version");
			if (!servers.isEmpty()) {
				List<InstallFiles> installFileList = installFilesRepository.findByServer(servers.getFirst());
				JSONArray jsonArray = new JSONArray();
				for (InstallFiles file : installFileList) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("fileName", file.getFileName());
					jsonObject.put("filePath", file.getFilePath());
					jsonObject.put("serverId", file.getServer().getId());
					jsonObject.put("fileType", file.getFileType().getFileType());
					jsonObject.put("fileTypeId", file.getFileType().getId());
					jsonArray.put(jsonObject);
				}
				mav.addObject("installFile", installFileList);
				mav.addObject("installFileList", jsonArray.toString());
			}
		} else if (stepId == 5) {
			mav.setViewName("installPage/choose-services");
			if (!servers.isEmpty()) {
				List<ServerRole> serverRoleList = serverRoleRepository.findAll();
				JSONArray jsonArray = new JSONArray();
				for (ServerRole role : serverRoleList) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("roleId", role.getId());
					jsonObject.put("roleName", role.getRoleName());
					jsonArray.put(jsonObject);
				}
				mav.addObject("servers", servers);
				mav.addObject("serverRoleList", jsonArray);
				mav.addObject("roleList", serverRoleList);
			}
		} else if (stepId == 6) {
			if (!servers.isEmpty()) {
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				for (int i = 0; i < servers.size(); i++) {
					map.put(servers.get(i).getHostname(), servers.get(i).getId());
				}
				mav.addObject(map);
			}
			mav.setViewName("installPage/install-start");
		} else {
			mav.setViewName("dashboard/main");
		}

		mav.addObject("stepId", stepId);
		return mav;
	}
}
