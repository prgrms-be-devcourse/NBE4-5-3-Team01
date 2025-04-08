package com.team01.project.user.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTokenProviderTest {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Test
	void testCreateToken() {
		String userId = "asdf1234";
		String spotifyAccessToken = "spotify-accesstoken";

		String token = jwtTokenProvider.generateJwtToken(userId, spotifyAccessToken);
		String extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

		System.out.println("토큰:" + token);
		System.out.println("토큰에서 추출한 사용자 ID:" + extractedUserId);
		assertNotNull(token);
		assertEquals(userId, extractedUserId);
	}

	@Test
	void testValidateTokenValid() {
		String userId = "asdf1234";
		String spotifyAccessToken = "spotify-accesstoken";
		String token = jwtTokenProvider.generateJwtToken(userId, spotifyAccessToken);
		System.out.println("서버토큰:" + token);
		boolean isValid = jwtTokenProvider.validateToken(token);

		assertTrue(isValid);
	}

	@Test
	void testValidateTokenInvaild() {

		String invalidToken = "invalid.token.value";

		boolean isValid = jwtTokenProvider.validateToken(invalidToken);

		assertFalse(isValid);
	}
}
