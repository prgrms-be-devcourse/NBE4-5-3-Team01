package com.team01.project.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/**") // 모든 요청에 대해 보안 적용
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers("/api/v1/user/login", "/api/v1/user/logout", "/api/v1/user/refresh",
						"/login/oauth2/code/spotify", "/").permitAll()
					.anyRequest().authenticated()) // 모든 요청에 대해 인증 필요
			.oauth2Login(oauth2 -> oauth2
					.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
					.defaultSuccessUrl("/api/v1", true) // 로그인 성공 시 리다이렉트 url
					.failureUrl("/login?error=true")// 로그인 실패 시 리다이렉트 url
				// .authorizationEndpoint(authorization ->
				// 	authorization.baseUri("/oauth2/authorization")) // OAuth 인증 경로
			);
		return http.build();
	}
}