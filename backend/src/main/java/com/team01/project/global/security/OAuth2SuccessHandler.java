package com.team01.project.global.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtTokenProvider jwtTokenProvider;

	public OAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		System.out.println("======= START OAuth2SuccessHandler.onAuthenticationSuccess =======");
		// OAuth2 인증된 사용자 정보 가져오기
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		String userId = oAuth2User.getAttribute("id");

		if (userId == null) {
			throw new RuntimeException("OAuth2 사용자 ID를 찾을 수 없습니다.");
		}

		// Spotify Access Token 가져오기
		String spotifyAccessToken = oAuth2User.getAttribute("spotifyToken");

		if (spotifyAccessToken == null || userId == null) {
			System.out.println("Spotify Access Token 또는 User ID 없음");
			response.sendRedirect("http://localhost:3000/login?error=token_not_found");
			return;
		}
		String jwtToken = oAuth2User.getAttribute("jwtToken");

		System.out.println("클라이언트로 전송 될 jwt:" + jwtToken);

		// 프론트엔드로 리다이렉트할 URL 생성
		String redirectUrl = "http://localhost:3000/login/callback"
			+ "?access_token=" + jwtToken
			+ "&spotify_access_token=" + spotifyAccessToken;

		System.out.println("OAuth2 성공 후 프론트엔드로 리다이렉트: " + redirectUrl);

		// 프론트엔드로 리다이렉트
		response.sendRedirect(redirectUrl);
	}

}
