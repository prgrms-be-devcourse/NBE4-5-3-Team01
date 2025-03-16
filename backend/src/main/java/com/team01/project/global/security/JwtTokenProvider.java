package com.team01.project.global.security;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.team01.project.domain.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtTokenProvider {
	private static final String SECRET_KEY_STRING =
		"aXlvdXZvLWNvc2VjLXJhbmdvbGV" + "0LXNlY3JldC1rZXkta2V5LWZvci1qd3Q="; // Base64 인코딩된 값
	private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY_STRING));
	private static final long VALIDITY_IN_MS = 3600000L; // 1시간
	// private static final long VALIDITY_IN_MS = 10000L; // 10초
	private long refreshTokenValidity = 1000 * 60 * 60 * 24 * 7; // 7일
	private User user;

	//jwt 토큰 생성
	public String generateJwtToken(String userId, String spotifyAccessToken) {
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
			.signWith(SECRET_KEY, SignatureAlgorithm.HS256) // ES256 → HS256으로 변경
			.compact();
	}

	// 리프레시 토큰 생성
	public String generateRefreshToken(String userId) {
		return Jwts.builder()
			.setSubject(userId)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
			.signWith(SECRET_KEY, SignatureAlgorithm.HS256)
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
			JwtParser parser = Jwts.parser()
				.setSigningKey(SECRET_KEY)
				.build();
			parser.parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			System.out.println("JWT 만료");
			return false;
		} catch (SignatureException e) {
			System.out.println("JWT 서명 검증 실패");
			return false;
		} catch (MalformedJwtException e) {
			System.out.println("JWT 형식이 올바르지 않음");
			return false;
		} catch (Exception e) {
			System.out.println("JWT 검증 중 알 수 없는 오류 발생: " + e.getMessage());
			return false;
		}
	}

	/**
	 * 주어진 JWT에서 'spotifyToken' 클레임을 추출합니다.
	 *
	 * @param jwtToken 클라이언트로부터 전달받은 JWT 토큰
	 * @return spotifyToken 값이 존재하면 반환, 그렇지 않으면 null
	 */
	public String extractSpotifyToken(String jwtToken) {
		try {
			JwtParser parser = Jwts.parser().setSigningKey(SECRET_KEY).build();
			Claims claims = parser.parseClaimsJws(jwtToken).getBody();

			// 토큰에 spotifyToken 클레임이 있는지 확인
			if (claims.get("spotifyToken") != null) {
				return claims.get("spotifyToken", String.class);
			} else {
				// 만약 spotifyToken 클레임이 없다면, 이 토큰은 refresh token일 가능성이 높으므로 추출하지 않음
				System.out.println("현재 토큰에는 스포티파이 토큰이 포함되어 있지 않습니다.");
				return null;
			}
		} catch (Exception e) {
			// 그 외 에러 처리
			System.err.println("Error parsing JWT: " + e.getMessage());
		}
		return null;
	}
}
