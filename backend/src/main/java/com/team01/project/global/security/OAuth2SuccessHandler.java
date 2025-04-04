package com.team01.project.global.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.team01.project.domain.user.entity.RefreshToken;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.RefreshTokenRepository;
import com.team01.project.domain.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	public OAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		log.info("======= START OAuth2SuccessHandler.onAuthenticationSuccess =======");
		// OAuth2 인증된 사용자 정보 가져오기
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		String userId = oAuth2User.getAttribute("id");
		String refreshTokenValue = oAuth2User.getAttribute("refreshToken");
		String spotifyRefreshTokenValue = "";

		if (userId == null) {
			throw new RuntimeException("OAuth2 사용자 ID를 찾을 수 없습니다.");
		}

		if (authentication instanceof OAuth2AuthenticationToken oAuth2Auth) {
			String userId2 = oAuth2Auth.getName();

			OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
				oAuth2Auth.getAuthorizedClientRegistrationId(), userId2);

			if (authorizedClient == null) {
				log.info("authorizedClient이 저장되지 않음 수동 저장 진행");

			}

			spotifyRefreshTokenValue = authorizedClient.getRefreshToken().getTokenValue();

			// log.info("리프레시토큰" + spotifyRefreshTokenValue);
			// log.info("액세스토큰" + authorizedClient.getAccessToken().getTokenValue());

			User foundUser = userRepository.findById(userId2).orElse(null);
			RefreshToken refreshToken = RefreshToken.builder()
				.user(foundUser)
				.refreshToken(refreshTokenValue)
				.spotifyRefreshToken(spotifyRefreshTokenValue)
				.createdAt(LocalDateTime.now())
				.build();

			log.info("리프레시 토큰을 저장합니다.");
			refreshTokenRepository.save(refreshToken);
		}

		// Spotify Access Token 가져오기
		String spotifyAccessToken = oAuth2User.getAttribute("spotifyToken");

		if (spotifyAccessToken == null || userId == null) {
			log.info("Spotify Access Token 또는 User ID 없음");
			response.sendRedirect("http://localhost:3000/login?error=token_not_found");
			return;
		}
		String jwtToken = oAuth2User.getAttribute("jwtToken");

		// log.info("클라이언트로 전송 될 jwt:" + jwtToken);
		// log.info("클라이언트로 전송 될 refreshToken:" + refreshTokenValue);

		// 프론트엔드로 리다이렉트할 URL 생성
		String redirectUrl =
			"http://localhost:3000/login/callback" + "?access_token=" + jwtToken + "&refresh_token="
				+ refreshTokenValue + "&spotify_access_token=" + spotifyAccessToken;

		// log.info("OAuth2 성공 후 프론트엔드로 리다이렉트: " + redirectUrl);

		// 프론트엔드로 리다이렉트
		response.sendRedirect(redirectUrl);
	}

}
