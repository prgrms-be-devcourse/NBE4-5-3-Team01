package com.team01.project.domain.user.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.notification.service.NotificationService;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.RefreshTokenRepository;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.global.security.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SpotifyRefreshTokenService spotifyRefreshTokenService;

	@Transactional
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("======= START CustomOAuth2UserService.loadUser =======");

		if (userRequest == null) {
			throw new RuntimeException("OAuth2UserRequest가 null입니다.");
		}

		if (userRequest.getAccessToken() == null) {
			throw new RuntimeException("spotify Access Token이 null입니다.");
		}

		OAuth2User user = delegate.loadUser(userRequest);
		String spotifyAccessToken = userRequest.getAccessToken().getTokenValue();

		// log.info("spotify access token:" + spotifyAccessToken);
		// log.info("User Attributes: " + user.getAttributes()); // OAuth 사용자 정보 확인

		String userId = user.getName();
		if (userId == null) {
			throw new RuntimeException("OAuth2 사용자 ID를 찾을 수 없습니다.");
		}

		log.info("OAuth2 User ID:{}", userId);
		User foundUser = userRepository.findById(userId).orElse(null);

		//db에 사용자 없을 시 생성
		if (foundUser == null) {
			foundUser = User.builder()
				.id(userId)
				.name(user.getAttribute("display_name"))
				.email(user.getAttribute("email"))
				.createdDate(LocalDateTime.now())
				.build();

			userRepository.save(foundUser);
			log.info("최초 로그인 사용자 저장:{}", userId);

			notificationService.createDefaultNotifications(foundUser);
			log.info("{}님의 알림이 생성되었습니다.", foundUser.getName());

			notificationService.initLoginNotifications(LocalTime.now(), foundUser);
		}

		Optional<User> matchId = userRepository.findById(userId);

		if (!matchId.isEmpty()) {
			log.info("리프레시 토큰 테이블에 동일한 유저 ID 있을 때 기존 리프레시 토큰 삭제");
			refreshTokenRepository.deleteByUserId(userId);
		}

		//JWT 발급
		String jwtToken = jwtTokenProvider.generateJwtToken(userId, spotifyAccessToken);
		String refreshToken = jwtTokenProvider.generateRefreshToken(userId);

		Map<String, Object> attributes = new HashMap<>(user.getAttributes());
		attributes.put("spotifyToken", spotifyAccessToken);
		attributes.put("jwtToken", jwtToken);
		attributes.put("refreshToken", refreshToken);

		log.info("유저서비스에서 생성된 jwt:{}", jwtToken);

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority("USER")),
			attributes,
			"id"
		);
	}

}
