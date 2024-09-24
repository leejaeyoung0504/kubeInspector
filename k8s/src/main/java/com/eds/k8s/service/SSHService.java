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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

@Service
public class SSHService {

	// SSH 연결 메서드
	public boolean connect(String ipAddress, int port, String user, String sshKey) {
		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();

		// SSH 키를 파일로 저장
		String sshKeyPath = saveKeyToFile(sshKey);

		ClientSession session = null;
		try {
			session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);
				System.out.println("SSH 연결 성공: " + ipAddress);
				return true;
			}
		} catch (Exception e) {
			System.err.println("SSH 연결 실패: " + e.getMessage());
		} finally {
			// 세션을 수동으로 닫음
			if (session != null) {
				try {
					session.close(); // 세션을 명시적으로 닫음
					session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10)); // 세션이 완전히 닫힐
																											// 때까지 대기
				} catch (Exception e) {
					System.err.println("세션 닫기 실패: " + e.getMessage());
				}
			}

			// 클라이언트 종료
			if (client != null && !client.isClosed()) {
				try {
					client.stop(); // 클라이언트도 명시적으로 종료
				} catch (Exception e) {
					System.err.println("클라이언트 종료 실패: " + e.getMessage());
				}
			}

			// SSH 키 파일 삭제
			deleteKeyFile(sshKeyPath);
		}
		return false;
	}

	// Hostname 확인 메서드
	public boolean verifyHostname(String expectedHostname, String ipAddress, int port, String user, String sshKey) {
		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();

		// SSH 키를 파일로 저장
		String sshKeyPath = saveKeyToFile(sshKey);
		ClientSession session = null;

		try {
			session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);

				String command = "hostname -f";
				String actualHostname = executeCommand(session, command).trim();

				return actualHostname.equals(expectedHostname);
			}
		} catch (Exception e) {
			System.err.println("Hostname 확인 실패: " + e.getMessage());
		} finally {
			// 세션을 수동으로 닫음
			if (session != null) {
				try {
					session.close(); // 세션을 명시적으로 닫음
					session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10)); // 세션이 완전히 닫힐
																											// 때까지 대기
				} catch (Exception e) {
					System.err.println("세션 닫기 실패: " + e.getMessage());
				}
			}

			// 클라이언트 종료
			if (client != null && !client.isClosed()) {
				try {
					client.stop(); // 클라이언트도 명시적으로 종료
				} catch (Exception e) {
					System.err.println("클라이언트 종료 실패: " + e.getMessage());
				}
			}

			// SSH 키 파일 삭제
			deleteKeyFile(sshKeyPath);
		}
		return false;
	}

	// NAS 디렉토리 확인 메서드
	public boolean checkNasDirectory(String nasDirectory, String ipAddress, int port, String user, String sshKey) {
		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();

		// SSH 키를 파일로 저장
		String sshKeyPath = saveKeyToFile(sshKey);
		ClientSession session = null;

		try {
			session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);

				String command = "[ -d " + nasDirectory + " ] && echo \"exists\" || echo \"not exists\"";
				String result = executeCommand(session, command).trim();

				return "exists".equals(result);
			}
		} catch (Exception e) {
			System.err.println("NAS 디렉토리 확인 실패: " + e.getMessage());
		} finally {
			// 세션을 수동으로 닫음
			if (session != null) {
				try {
					session.close(); // 세션을 명시적으로 닫음
					session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10)); // 세션이 완전히 닫힐
																											// 때까지 대기
				} catch (Exception e) {
					System.err.println("세션 닫기 실패: " + e.getMessage());
				}
			}

			// 클라이언트 종료
			if (client != null && !client.isClosed()) {
				try {
					client.stop(); // 클라이언트도 명시적으로 종료
				} catch (Exception e) {
					System.err.println("클라이언트 종료 실패: " + e.getMessage());
				}
			}

			// SSH 키 파일 삭제
			deleteKeyFile(sshKeyPath);
		}
		return false;
	}

	// Containerd 설치
	public boolean installContainerd(String containerdPath, String nfsPath, String ipAddress, int port, String user,
			String sshKey) {

		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();

		// SSH 키를 파일로 저장
		String sshKeyPath = saveKeyToFile(sshKey);
		ClientSession session = null;

		try {
			session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);

				// 여러 명령어를 &&로 연결하여 실행
				String command = String.join(" && ", "tar Cxzvf /usr/local " + containerdPath,
						"mkdir -p /usr/local/lib/systemd/system/",
						"cp -rp " + nfsPath + "/containerd.service /usr/local/lib/systemd/system/",
						"mkdir -p /etc/containerd", "containerd config default > /etc/containerd/config.toml",
						"systemctl daemon-reload", "systemctl enable --now containerd");

				// 명령어 실행
				int exitStatus = executeCommandByK8s(session, command);
				return exitStatus == 0; // exitStatus 0은 성공을 의미
			}
		} catch (Exception e) {
			System.err.println("Containerd 설치 실패: " + e.getMessage());
		} finally {
			// 세션을 수동으로 닫음
			if (session != null) {
				try {
					session.close(); // 세션을 명시적으로 닫음
					session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10)); // 세션이 완전히 닫힐
																											// 때까지 대기
				} catch (Exception e) {
					System.err.println("세션 닫기 실패: " + e.getMessage());
				}
			}

			// 클라이언트 종료
			if (client != null && !client.isClosed()) {
				try {
					client.stop(); // 클라이언트도 명시적으로 종료
				} catch (Exception e) {
					System.err.println("클라이언트 종료 실패: " + e.getMessage());
				}
			}

			// SSH 키 파일 삭제
			deleteKeyFile(sshKeyPath);
		}

		return false;
	}

	// runc 설치
	public boolean installRunc(String runcPath, String ipAddress, int port, String user, String sshKey) {

		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();

		// SSH 키를 파일로 저장
		String sshKeyPath = saveKeyToFile(sshKey);

		ClientSession session = null;
		try {
			// 세션 생성
			session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);

				String command = "install -m 755 " + runcPath + " /usr/local/sbin/runc";

				// 명령어 실행
				int exitStatus = executeCommandByK8s(session, command);
				return exitStatus == 0; // exitStatus 0은 성공을 의미
			}
		} catch (Exception e) {
			System.err.println("Runc 설치 실패: " + e.getMessage());
		} finally {
			// 세션을 수동으로 닫음
			if (session != null) {
				try {
					session.close(); // 세션을 명시적으로 닫음
					session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10)); // 세션이 완전히 닫힐
																											// 때까지 대기
				} catch (Exception e) {
					System.err.println("세션 닫기 실패: " + e.getMessage());
				}
			}

			// 클라이언트 종료
			if (client != null && !client.isClosed()) {
				try {
					client.stop(); // 클라이언트도 명시적으로 종료
				} catch (Exception e) {
					System.err.println("클라이언트 종료 실패: " + e.getMessage());
				}
			}

			// SSH 키 파일 삭제
			deleteKeyFile(sshKeyPath);
		}

		return false;
	}

	// cni 설치
	public boolean installCni(String cniPath, String ipAddress, int port, String user, String sshKey) {

		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();

		// SSH 키를 파일로 저장
		String sshKeyPath = saveKeyToFile(sshKey);
		ClientSession session = null;
		try {
			session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);

				String command = String.join(" && ", "mkdir -p /opt/cni/bin", "tar Cxzvf /opt/cni/bin " + cniPath);

				// 명령어 실행
				int exitStatus = executeCommandByK8s(session, command);
				return exitStatus == 0; // exitStatus 0은 성공을 의미
			}
		} catch (Exception e) {
			System.err.println("Runc 설치 실패: " + e.getMessage());
		} finally {
			// 세션을 수동으로 닫음
			if (session != null) {
				try {
					session.close(); // 세션을 명시적으로 닫음
					session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10)); // 세션이 완전히 닫힐
																											// 때까지 대기
				} catch (Exception e) {
					System.err.println("세션 닫기 실패: " + e.getMessage());
				}
			}

			// 클라이언트 종료
			if (client != null && !client.isClosed()) {
				try {
					client.stop(); // 클라이언트도 명시적으로 종료
				} catch (Exception e) {
					System.err.println("클라이언트 종료 실패: " + e.getMessage());
				}
			}

			// SSH 키 파일 삭제
			deleteKeyFile(sshKeyPath);
		}

		return false;
	}

	// K8s 설치
	public boolean installK8s(String nasPath, String ipAddress, int port, String user, String sshKey) {

		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();

		// SSH 키를 파일로 저장
		String sshKeyPath = saveKeyToFile(sshKey);
		ClientSession session = null;
		try {
			session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);

				String command = String.join(" && ", "mkdir -p /usr/local/bin",
						"cp -rp " + nasPath + "/kubeadm /usr/local/bin",
						"cp -rp " + nasPath + "/kubelet /usr/local/bin",
						"cp -rp " + nasPath + "/kubectl /usr/local/bin",
						"mkdir -p /etc/systemd/system/kubelet.service.d",
						"cp -rp " + nasPath + "/kubelet.service /etc/systemd/system/",
						"cp -rp " + nasPath + "/10-kubeadm.conf /etc/systemd/system/kubelet.service.d/",
						"systemctl enable --now kubelet");

				// 명령어 실행
				int exitStatus = executeCommandByK8s(session, command);
				return exitStatus == 0; // exitStatus 0은 성공을 의미
			}
		} catch (Exception e) {
			System.err.println("Runc 설치 실패: " + e.getMessage());
		} finally {
			// 세션을 수동으로 닫음
			if (session != null) {
				try {
					session.close(); // 세션을 명시적으로 닫음
					session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10)); // 세션이 완전히 닫힐
																											// 때까지 대기
				} catch (Exception e) {
					System.err.println("세션 닫기 실패: " + e.getMessage());
				}
			}

			// 클라이언트 종료
			if (client != null && !client.isClosed()) {
				try {
					client.stop(); // 클라이언트도 명시적으로 종료
				} catch (Exception e) {
					System.err.println("클라이언트 종료 실패: " + e.getMessage());
				}
			}

			// SSH 키 파일 삭제
			deleteKeyFile(sshKeyPath);
		}

		return false;
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

	// 명령어 실행 메서드
	private int executeCommandByK8s(ClientSession session, String command) throws IOException {
		try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
				ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
				ChannelExec channel = session.createExecChannel(command)) {

			channel.setOut(responseStream);
			channel.open().verify(10, TimeUnit.SECONDS);

			// 명령이 완료될 때까지 기다림
			channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(10));

			// stdout과 stderr의 내용을 반환 (stderr가 있으면 추가로 출력)
			String stdout = responseStream.toString();
			String stderr = errorStream.toString();

			if (!stderr.isEmpty()) {
				System.err.println("Error Output: " + stderr); // stderr 출력
			}

			// 명령어의 exit status 반환
			return channel.getExitStatus();
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

	// NAS 디렉토리에서 파일 목록 가져오기
	public List<String> getNasFiles(String nasDirectory, String ipAddress, int port, String user, String sshKey) {
		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();
		List<String> fileList = new ArrayList<>();

		String sshKeyPath = saveKeyToFile(sshKey);
		ClientSession session = null;
		try {
			session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);

				String command = "ls -al " + nasDirectory;
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
			}
		} catch (Exception e) {
			System.err.println("NAS 디렉토리 파일 조회 실패: " + e.getMessage());
		} finally {
			// 세션을 수동으로 닫음
			if (session != null) {
				try {
					session.close(); // 세션을 명시적으로 닫음
					session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10)); // 세션이 완전히 닫힐
																											// 때까지 대기
				} catch (Exception e) {
					System.err.println("세션 닫기 실패: " + e.getMessage());
				}
			}

			// 클라이언트 종료
			if (client != null && !client.isClosed()) {
				try {
					client.stop(); // 클라이언트도 명시적으로 종료
				} catch (Exception e) {
					System.err.println("클라이언트 종료 실패: " + e.getMessage());
				}
			}

			// SSH 키 파일 삭제
			deleteKeyFile(sshKeyPath);
		}
		return fileList;
	}

	// kubeadm init 명령어 실행 메서드
	public String executeKubeadmInit(String ipAddress, int port, String user, String sshKey) {
	    SshClient client = SshClient.setUpDefaultClient();
	    client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
	    client.start();

	    String sshKeyPath = saveKeyToFile(sshKey);
	    ClientSession session = null;
	    try {
	        System.out.println("Creating SSH session for kubeadm init...");
	        session = createSession(client, ipAddress, port, user, sshKeyPath);
	        if (session != null) {
	            session.auth().verify(10, TimeUnit.SECONDS);
	            System.out.println("Executing kubeadm init command...");

	            // kubeadm init 명령어 실행
	            String command = "kubeadm init --pod-network-cidr=10.244.0.0/16 --v=5";
	            String result = executeCommands(session, command).trim();
	            System.out.println("kubeadm init result: " + result);

	            if (!result.isEmpty()) {
	                return result;
	            }
	        }
	    } catch (Exception e) {
	        System.err.println("kubeadm init 실행 실패: " + e.getMessage());
	        return "error";
	    } finally {
	        if (session != null) {
	            try {
	                session.close();
	                session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10));
	                System.out.println("Session closed.");
	            } catch (Exception e) {
	                System.err.println("Session close failed: " + e.getMessage());
	            }
	        }

	        if (client != null && !client.isClosed()) {
	            try {
	                client.stop();
	                System.out.println("Client stopped.");
	            } catch (Exception e) {
	                System.err.println("Client stop failed: " + e.getMessage());
	            }
	        }

	        deleteKeyFile(sshKeyPath);
	        System.out.println("SSH key file deleted.");
	    }
	    return "error";
	}

	// kubeadm mkdir 명령어 실행 메서드
	public boolean executeKubeadmMkdir(String mkdirCommand, String ipAddress, int port, String user, String sshKey) {
		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();

		String sshKeyPath = saveKeyToFile(sshKey);
		ClientSession session = null;
		try {
			session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);

				// kubeadm 여러 명령어 실행
				int exitStatus = executeCommandByK8s(session, mkdirCommand);
				return exitStatus == 0; // exitStatus 0은 성공을 의미

			}
		} catch (Exception e) {
			System.err.println("kubeadm join 실행 실패: " + e.getMessage());
			return false;
		} finally {
			// 세션을 수동으로 닫음
			if (session != null) {
				try {
					session.close(); // 세션을 명시적으로 닫음
					session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10)); // 세션이 완전히 닫힐
																											// 때까지 대기
				} catch (Exception e) {
					System.err.println("세션 닫기 실패: " + e.getMessage());
				}
			}

			// 클라이언트 종료
			if (client != null && !client.isClosed()) {
				try {
					client.stop(); // 클라이언트도 명시적으로 종료
				} catch (Exception e) {
					System.err.println("클라이언트 종료 실패: " + e.getMessage());
				}
			}

			// SSH 키 파일 삭제
			deleteKeyFile(sshKeyPath);
		}
		return false;
	}

	// kubeadm join 명령어 실행 메서드
	public String executeKubeadmJoin(String joinCommand, String ipAddress, int port, String user, String sshKey) {
		SshClient client = SshClient.setUpDefaultClient();
		client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
		client.start();

		String sshKeyPath = saveKeyToFile(sshKey);

		ClientSession session = null;

		try {
			session = createSession(client, ipAddress, port, user, sshKeyPath);
			if (session != null) {
				session.auth().verify(10, TimeUnit.SECONDS);

				// kubeadm join 명령어 실행
				String result = executeCommand(session, joinCommand).trim();

				if (!result.isEmpty()) {
					// 성공하면 결과 반환
					return result;
				}
			}
		} catch (Exception e) {
			System.err.println("kubeadm join 실행 실패: " + e.getMessage());
			return "error";
		} finally {
			// 세션을 수동으로 닫음
			if (session != null) {
				try {
					session.close(); // 세션을 명시적으로 닫음
					session.waitFor(EnumSet.of(ClientSessionEvent.CLOSED), TimeUnit.SECONDS.toMillis(10)); // 세션이 완전히 닫힐
																											// 때까지 대기
				} catch (Exception e) {
					System.err.println("세션 닫기 실패: " + e.getMessage());
				}
			}

			// 클라이언트 종료
			if (client != null && !client.isClosed()) {
				try {
					client.stop(); // 클라이언트도 명시적으로 종료
				} catch (Exception e) {
					System.err.println("클라이언트 종료 실패: " + e.getMessage());
				}
			}

			// SSH 키 파일 삭제
			deleteKeyFile(sshKeyPath);
		}
		return "error";
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
		}
	}

}