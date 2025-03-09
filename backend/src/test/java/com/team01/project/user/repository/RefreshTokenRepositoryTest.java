package com.team01.project.user.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.team01.project.domain.user.entity.RefreshToken;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.RefreshTokenRepository;
import com.team01.project.domain.user.repository.UserRepository;

@SpringBootTest
public class RefreshTokenRepositoryTest {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void testCreateRefreshToken() {
		Optional<User> userOptional = userRepository.findById("1234");
		if (userOptional.isEmpty()) {
			System.out.println("유저 아이디 중 1234가 없음.");
			return;
		}

		User user = userOptional.get();

		if (user != null) {
			RefreshToken refreshToken = RefreshToken.builder()
				.user(user)
				.refreshToken("sampleRefreshToken")
				.createdAt(LocalDateTime.now())
				.build();

			RefreshToken saveToken = refreshTokenRepository.save(refreshToken);
			System.out.println("리프레시 토큰 저장 완료:" + saveToken.getRefreshToken());
		}
	}
}
