package com.team01.project.domain.user.service;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyRefreshTokenService {

	@Value("${spring.security.oauth2.client.registration.spotify.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.spotify.client-secret}")
	private String clientSecret;

	private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";

	public String refreshAccessToken(String refreshToken) {
		System.out.println("===== START SpotifyRefreshTokenService.refreshAccessToken =====");

		// client_id와 client_secret을 ':'으로 구분하여 Basic 인증 헤더 준비
		String credentials = clientId + ":" + clientSecret;
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + encodedCredentials);
		headers.set("Content-Type", "application/x-www-form-urlencoded");

		// 요청 본문 구성 (grant_type과 refresh_token을 사용)
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "refresh_token");
		body.add("refresh_token", refreshToken);

		// HttpEntity로 요청 본문과 헤더를 설정
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

		// RestTemplate을 사용하여 POST 요청 전송
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, entity, String.class);

		// 응답 출력
		System.out.println("리프레시 토큰 응답: " + response.getBody());

		// 액세스 토큰을 반환하거나 처리
		return response.getBody();
	}
}
