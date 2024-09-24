package com.eds.k8s.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.eds.k8s.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email); // 이메일로 사용자 찾기
}