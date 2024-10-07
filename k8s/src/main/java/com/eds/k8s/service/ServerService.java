package com.eds.k8s.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.eds.k8s.model.Server;
import com.eds.k8s.model.User;
import com.eds.k8s.repository.ServerRepository;

@Service
public class ServerService {

	private final ServerRepository serverRepository;

	public ServerService(ServerRepository serverRepository) {
		this.serverRepository = serverRepository;
	}

	public void saveServers(User user, String hostnames, String encryptedSshKey, int sshPort, String sshUser,
			String nasDirectory) {
		String[] hostnameEntries = hostnames.split("\n");

		serverRepository.deleteAll();
		for (String entry : hostnameEntries) {
			String[] parts = entry.split(":");
			String hostname = parts[0].trim();
			String ipAddress = parts[1].trim();

			Optional<Server> existingServer = serverRepository.findByUserIdAndHostname(user.getUserId(), hostname);

			Server server;
			if (existingServer.isPresent()) {
				server = existingServer.get();
			} else {
				server = new Server();
				server.setUser(user);
			}

			server.setHostname(hostname);
			server.setIpAddress(ipAddress);
			server.setSshPort(sshPort);
			server.setSshUser(sshUser);
			server.setSshKey(encryptedSshKey);
			server.setNasDirectory(nasDirectory);
			server.setStatus(Server.Status.PENDING);

			serverRepository.save(server);
		}
	}

	// 현재 로그인한 사용자 정보
	public Server findServierByServerId(Integer serverId) {
		return serverRepository.findById(serverId).orElseThrow(() -> new IllegalArgumentException("서버를 찾을 수 없습니다."));
	}
}
