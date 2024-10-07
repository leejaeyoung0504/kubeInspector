package com.eds.k8s.service;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.session.ClientSession.ClientSessionEvent;
import org.apache.sshd.common.config.keys.loader.KeyPairResourceLoader;
import org.apache.sshd.common.util.io.resource.PathResource;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.springframework.stereotype.Service;

import com.eds.k8s.model.Server;

@Service
public class SSHServiceNew {

	// 세션을 관리하기 위한 맵 (동시성 보장)
	private static final Map<String, ClientSession> sessionMap = new ConcurrentHashMap<>();

	// SSH 연결 메서드
	public boolean connect(String ipAddress, int port, String user, String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		if (session != null && session.isOpen()) {
			System.out.println("SSH 연결 성공: " + ipAddress);
			return true;
		} else {
			System.err.println("SSH 연결 실패");
			return false;
		}
	}

	// 세션을 가져오는 메서드 (세션이 없으면 생성)
	private ClientSession getSession(String ipAddress, int port, String user, String sshKey) {
		String key = ipAddress + ":" + port;

		// 이미 세션이 존재하고 열려있으면 해당 세션 반환
		if (sessionMap.containsKey(key) && sessionMap.get(key).isOpen()) {
			System.out.println("기존 SSH 세션 사용: " + key);
			return sessionMap.get(key);
		}

		// 새로운 세션 생성
		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();

		String sshKeyPath = saveKeyToFile(sshKey);
		try {
			ClientSession session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);
				sessionMap.put(key, session); // 세션 저장
				System.out.println("새로운 SSH 세션 생성: " + key);
				return session;
			}
		} catch (Exception e) {
			System.err.println("SSH 세션 생성 실패: " + e.getMessage());
		}

		return null;
	}

	// 세션 생성 메서드
	private ClientSession createSession(SshClient client, String ipAddress, int port, String user, String sshKeyPath)
			throws IOException, GeneralSecurityException {
		ClientSession session = client.connect(user, ipAddress, port).verify(10, TimeUnit.SECONDS).getSession();
		KeyPair keyPair = loadKeyPair(Paths.get(sshKeyPath));
		session.addPublicKeyIdentity(keyPair);
		return session;
	}

	// KeyPair 로딩 메서드
	private KeyPair loadKeyPair(Path sshKeyPath) throws IOException, GeneralSecurityException {
		PathResource resource = new PathResource(sshKeyPath);
		KeyPairResourceLoader loader = SecurityUtils.getKeyPairResourceParser();
		Iterable<KeyPair> keyPairs = loader.loadKeyPairs(null, resource, null);
		return keyPairs.iterator().next();
	}

	// 세션 종료 메서드 (필요 시 호출)
	public void closeSession(String ipAddress, int port) {
		String key = ipAddress + ":" + port;
		ClientSession session = sessionMap.get(key);
		if (session != null && session.isOpen()) {
			try {
				session.close();
				session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10));
				sessionMap.remove(key); // 세션 맵에서 제거
				System.out.println("SSH 세션 종료: " + key);
			} catch (Exception e) {
				System.err.println("SSH 세션 종료 실패: " + e.getMessage());
			}
		}
	}

	// Hostname 확인 메서드
	public boolean verifyHostname(String expectedHostname, String ipAddress, int port, String user, String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		if (session != null && session.isOpen()) {
			String command = "hostname -f";
			try {
				String actualHostname = executeCommand(session, command).trim();
				return actualHostname.equals(expectedHostname);
			} catch (Exception e) {
				System.err.println("Hostname 확인 실패: " + e.getMessage());
			} finally {
				closeSession(ipAddress, port);
			}
		}
		return false;
	}

	public boolean confiemHost(Server server, String sshKey) {

		ClientSession session = getSession(server.getIpAddress(), server.getSshPort(), server.getSshUser(), sshKey);
		if (session != null && session.isOpen()) {
			// System.out.println("SSH 연결 성공: " + ipAddress);
			return true;
		} else {
			System.err.println("SSH 연결 실패");
			return false;
		}

	}

	// Containerd 설치
	public boolean installContainerd(String containerdPath, String nfsPath, String ipAddress, int port, String user,
			String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		if (session != null && session.isOpen()) {
			String command = String.join(" ; ", "tar Cxzvf /usr/local " + containerdPath,
					"mkdir -p /usr/local/lib/systemd/system/",
					"cp -rp " + nfsPath + "/containerd.service /usr/local/lib/systemd/system/",
					"mkdir -p /etc/containerd", "containerd config default > /etc/containerd/config.toml",
					"systemctl daemon-reload", "systemctl enable --now containerd");

			try {
				System.out.println("Debug Test");
				int exitStatus = executeCommandByK8s(session, command);
				return exitStatus == 0;
			} catch (Exception e) {
				System.err.println("Containerd 설치 실패: " + e.getMessage());
			} finally {
				System.out.println("Debug Test Finally IP:" + ipAddress);
				closeSession(ipAddress, port);
			}
		}
		return false;
	}

	// runc 설치
	public boolean installRunc(String runcPath, String ipAddress, int port, String user, String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		if (session != null && session.isOpen()) {
			String command = "install -m 755 " + runcPath + " /usr/local/sbin/runc";

			try {
				int exitStatus = executeCommandByK8s(session, command);
				return exitStatus == 0;
			} catch (Exception e) {
				System.err.println("runc 설치 실패: " + e.getMessage());
			} finally {
				closeSession(ipAddress, port);
			}
		}
		return false;
	}

	// cni 설치
	public boolean installCni(String cniPath, String ipAddress, int port, String user, String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		if (session != null && session.isOpen()) {
			String command = String.join(" && ", "mkdir -p /opt/cni/bin", "tar Cxzvf /opt/cni/bin " + cniPath);

			try {
				int exitStatus = executeCommandByK8s(session, command);
				return exitStatus == 0;
			} catch (Exception e) {
				System.err.println("cni 설치 실패: " + e.getMessage());
			} finally {
				closeSession(ipAddress, port);
			}
		}
		return false;
	}

	// K8s 설치
	public boolean installK8s(String nasPath, String ipAddress, int port, String user, String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		if (session != null && session.isOpen()) {
			String command = String.join(" && ", "mkdir -p /usr/local/bin",
					"cp -rp " + nasPath + "/kubeadm /usr/local/bin", "cp -rp " + nasPath + "/kubelet /usr/local/bin",
					"cp -rp " + nasPath + "/kubectl /usr/local/bin", "mkdir -p /etc/systemd/system/kubelet.service.d",
					"cp -rp " + nasPath + "/kubelet.service /etc/systemd/system/",
					"cp -rp " + nasPath + "/10-kubeadm.conf /etc/systemd/system/kubelet.service.d/",
					"systemctl enable --now kubelet");

			try {
				int exitStatus = executeCommandByK8s(session, command);
				return exitStatus == 0;
			} catch (Exception e) {
				System.err.println("k8s 설치 실패: " + e.getMessage());
			} finally {
				closeSession(ipAddress, port);
			}
		}
		return false;
	}

	// NAS 디렉토리에서 파일 목록 가져오기
	public List<String> getNasFiles(String nasDirectory, String ipAddress, int port, String user, String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		List<String> fileList = new ArrayList<>();

		if (session != null && session.isOpen()) {
			String command = "ls -al " + nasDirectory;

			try {
				String result = executeCommand(session, command).trim();
				// 파일 목록 파싱
				String[] files = result.split("\n");
				for (String file : files) {
					// 파일명만 추출 (ls 결과에서 마지막 부분)
					String[] fileInfo = file.split("\\s+");
					if (fileInfo.length > 8) {
						String fileName = fileInfo[fileInfo.length - 1];
						fileList.add(fileName);
					}
				}
			} catch (Exception e) {
				System.err.println("nas dir file dir List 검색 실패: " + e.getMessage());
			} finally {
				closeSession(ipAddress, port);
			}
		}
		return fileList;
	}

	// kube init 설치
	public String executeKubeadmInit(String ipAddress, int port, String user, String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		if (session != null && session.isOpen()) {

			String command = String.join(" && ", "mkdir -p /var/lib/kubelet",
					"kubeadm init --pod-network-cidr=10.244.0.0/16 --v=5");
			try {

				String result = executeCommands(session, command).trim();
				if (!result.isEmpty()) {
					return result;
				}
			} catch (Exception e) {
				System.err.println("kube init 설치 실패: " + e.getMessage());
			} finally {
				closeSession(ipAddress, port);
			}
		}
		return "error";
	}

	// kubeadm mkdir 명령어 실행 메서드
	public boolean executeKubeadmMkdir(String mkdirCommand, String ipAddress, int port, String user, String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		if (session != null && session.isOpen()) {
			try {

				// kubeadm 여러 명령어 실행
				int exitStatus = executeCommandByK8s(session, mkdirCommand);
				return exitStatus == 0; // exitStatus 0은 성공을 의미
			} catch (Exception e) {
				System.err.println("kubeadm mkdir 설치 실패: " + e.getMessage());
			} finally {
				closeSession(ipAddress, port);
			}
		}
		return false;
	}

	// kubeadm join 명령어 실행 메서드
	public String executeKubeadmJoin(String joinCommand, String ipAddress, int port, String user, String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		if (session != null && session.isOpen()) {
			try {
				// kubeadm join 명령어` 실행
				String result = executeCommand(session, joinCommand).trim();
				if (!result.isEmpty()) {
					// 성공하면 결과 반환
					return result;
				}
			} catch (Exception e) {
				System.err.println("kubeadm join 설치 실패: " + e.getMessage());
			} finally {
				closeSession(ipAddress, port);
			}
		}
		return "error";
	}

	// NAS 디렉토리 확인 메서드
	public boolean checkNasDirectory(String nasDirectory, String ipAddress, int port, String user, String sshKey) {
		ClientSession session = getSession(ipAddress, port, user, sshKey);
		if (session != null && session.isOpen()) {
			String command = "[ -d " + nasDirectory + " ] && echo \"exists\" || echo \"not exists\"";
			try {
				String result = executeCommand(session, command).trim();

				return "exists".equals(result);
			} catch (Exception e) {
				System.err.println("nas dir 검색 실패: " + e.getMessage());
			} finally {
				closeSession(ipAddress, port);
			}
		}
		return false;
	}

	private String executeCommands(ClientSession session, String command) throws IOException {
		System.out.println("명령어 실행 중: {}" + command);

		try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
				ChannelExec channel = session.createExecChannel(command)) {

			channel.setOut(responseStream);
			channel.setErr(responseStream); // 오류 로그도 함께 수집
			channel.open().verify(10, TimeUnit.SECONDS);

			// 채널이 닫힐 때까지 기다림 (필요시 시간 조정)
			channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(600));

			// 명령 실행 중 실시간으로 출력 내용을 읽어오기
			String output = responseStream.toString();
			System.out.println("명령어 실행 결과: {}" + output);

			return output;
		} catch (Exception e) {
			System.out.println("명령어 실행 중 오류 발생: {}" + e.getMessage() + " e");
			return "";
		} finally {
			System.out.println("Debug Test Finally finally init:");
		}
	}

	// 기타 메서드들도 동일한 방식으로 세션을 관리하며 작업
	// Kubeadm init, runc 설치 등 메서드에서 동일한 getSession 메서드를 통해 세션을 재사용합니다.

	// 명령어 실행 메서드
	private String executeCommand(ClientSession session, String command) throws IOException {
		try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
				ChannelExec channel = session.createExecChannel(command)) {

			channel.setOut(responseStream);
			channel.open().verify(10, TimeUnit.SECONDS);

			channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(10));
			return responseStream.toString();
		}
	}

	// 명령어 실행 메서드 (Exit Status 확인)
	private int executeCommandByK8s(ClientSession session, String command) throws IOException {
		try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
				ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
				ChannelExec channel = session.createExecChannel(command)) {

			channel.setOut(responseStream);
			channel.setErr(errorStream);
			channel.open().verify(10, TimeUnit.SECONDS);

			channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(10));
			int exitStatus = channel.getExitStatus();

			String errorOutput = errorStream.toString();
			if (!errorOutput.isEmpty()) {
				System.out.println("Error: " + errorOutput);
			}

			return exitStatus;
		}
	}

	// SSH 키를 파일로 저장하는 메서드
	private String saveKeyToFile(String sshKey) {
		try {
			Path tempFile = Files.createTempFile("ssh-key", ".pem");
			try (FileWriter writer = new FileWriter(tempFile.toFile())) {
				writer.write(sshKey);
			}
			return tempFile.toAbsolutePath().toString();
		} catch (IOException e) {
			throw new RuntimeException("SSH 키 파일 저장 중 오류 발생", e);
		}
	}

	// SSH 키 파일을 삭제하는 메서드
	private void deleteKeyFile(String sshKeyPath) {
		try {
			Files.deleteIfExists(Paths.get(sshKeyPath));
		} catch (IOException e) {
			System.err.println("SSH 키 파일 삭제 중 오류 발생: " + e.getMessage());
		}
	}
}