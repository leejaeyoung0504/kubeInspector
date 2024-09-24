package com.eds.k8s.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eds.k8s.model.User;
import com.eds.k8s.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void createUser(String email, String rawPassword, String role, Integer userId) {
		String encodedPassword = passwordEncoder.encode(rawPassword);
		User user = new User(email, encodedPassword, role, userId);
		userRepository.save(user);
	}
	
	// 이메일을 통해 사용자 ID를 가져오는 메서드
    public Long getUserIdFromEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get().getId(); // 사용자 ID 반환
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}