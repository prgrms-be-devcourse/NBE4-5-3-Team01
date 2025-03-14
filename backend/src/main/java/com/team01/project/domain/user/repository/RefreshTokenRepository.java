package com.team01.project.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team01.project.domain.user.entity.RefreshToken;
import com.team01.project.domain.user.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByUserId(String userId);

	Optional<RefreshToken> findTopByUserIdOrderByCreatedAtDesc(String userId);

	void deleteByUserId(String userId);

	String user(User user);
}
