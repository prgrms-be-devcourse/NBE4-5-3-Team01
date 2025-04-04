package com.team01.project.domain.user.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpotifyRefreshTokenService {

	@Value("${spring.security.oauth2.client.registration.spotify.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.spotify.client-secret}")
	private String clientSecret;

	private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";

	private final RestTemplate restTemplate = new RestTemplate();

	public String refreshAccessToken(String refreshToken) {
		log.info("===== START SpotifyRefreshTokenService.refreshAccessToken =====");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth(clientId, clientSecret); // 클라이언트 ID, 시크릿으로 인증

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("code", refreshToken);
		body.add("redirect_uri", "http://localhost:3000/login/callback");

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
		ResponseEntity<Map> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, requestEntity, Map.class);

		if (response.getBody() != null && response.getBody().containsKey("refresh_token")) {
			return response.getBody().get("refresh_token").toString();
		}
		throw new RuntimeException("리프레시 토큰을 가져오지 못했습니다.");
	}
}
