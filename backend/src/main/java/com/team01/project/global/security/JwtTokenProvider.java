package com.team01.project.global.security;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.team01.project.domain.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
	private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private static final long VALIDITY_IN_MS = 3600000L; // 1시간\
	private User user;

	//jwt 토큰 생성
	public String createToken(String userId, String spotifyAccessToken) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("sub", userId); // subject 설정
		claims.put("spotifyToken", spotifyAccessToken); //추가 Claims

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime validity = now.plus(Duration.ofMillis(VALIDITY_IN_MS));

		Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		Date expiration = Date.from(validity.atZone(ZoneId.systemDefault()).toInstant());

		return Jwts.builder()
			.addClaims(claims) // Immutable 오류 방지
			.setIssuedAt(issuedAt)
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS256, SECRET_KEY) // ES256 → HS256으로 변경
			.compact();
	}

	//jwt 토큰 검증 및 사용자 정보 추출
	public String getUserIdFromToken(String token) {

		JwtParser parser = Jwts.parser().setSigningKey(SECRET_KEY).build();
		Claims claims = parser.parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	//jwt 토큰 유효한 형식인 지 검증
	public boolean validateToken(String token) {
		try {
			JwtParser parser = Jwts.parser().setSigningKey(SECRET_KEY).build();
			parser.parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}
}
