package com.team01.project.domain.user.service;

import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.team01.project.domain.user.entity.RefreshToken;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.RefreshTokenRepository;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.global.security.JwtTokenProvider;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
	@Autowired
	private UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		if (userRequest == null) {
			throw new RuntimeException("OAuth2UserRequest가 null입니다.");
		}

		if (userRequest.getAccessToken() == null) {
			throw new RuntimeException("OAuth2 Access Token이 null입니다.");
		}

		OAuth2User user = delegate.loadUser(userRequest);
		String accessToken = userRequest.getAccessToken().getTokenValue();
		System.out.println("access token:" + accessToken);
		System.out.println("User Attributes: " + user.getAttributes()); // OAuth 사용자 정보 확인

		//리프레시 토큰 저장 ( DB에 저장 )
		String userId = user.getName();
		if (userId == null) {
			throw new RuntimeException("OAuth2 사용자 ID를 찾을 수 없습니다.");
		}

		System.out.println("OAuth2 User ID: " + userId);
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
			System.out.println("최초 로그인 사용자 저장:" + userId);
		}

		RefreshToken refreshToken = RefreshToken.builder()
			.user(foundUser)
			.refreshToken(accessToken)
			.createdAt(LocalDateTime.now())
			.build();

		refreshTokenRepository.save(refreshToken);

		//JWT 발급
		String jwtToken = jwtTokenProvider.createToken(userId, accessToken);

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority("USER")),
			user.getAttributes(),
			"id"
		);
	}
}
