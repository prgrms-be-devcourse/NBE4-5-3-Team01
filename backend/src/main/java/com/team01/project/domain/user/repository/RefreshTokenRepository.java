package com.team01.project.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.user.entity.RefreshToken;
import com.team01.project.domain.user.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByUserId(String userId);

	Optional<RefreshToken> findByUser(User user);

	Optional<RefreshToken> findTopByUserIdOrderByCreatedAtDesc(String userId);

	@Modifying
	@Transactional
	@Query("DELETE FROM RefreshToken r WHERE r.user.id = :userId")
	void deleteByUserId(String userId);

	String user(User user);
}
