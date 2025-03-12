package com.team01.project.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter) throws Exception {
		System.out.println("======= START SeCurityFilterChain =======");
		http
			.securityMatcher("/**") // 모든 요청에 대해 보안 적용
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(
				authorizeRequests -> authorizeRequests.requestMatchers("/api/v1/user/login", "/api/v1/logout",
						"/api/v1/user/refresh", "/api/v1/error", "/login", "/")
					.permitAll()
					.anyRequest()
					.authenticated()) // 모든 요청에 대해 인증 필요
			//.anyRequest().permitAll()
			.sessionManagement(
				session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용 시 세션 비활성화
			.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class) // JWT필터 추가 ( JWT token 검증 )
			.oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
					.successHandler(oAuth2SuccessHandler)
					//	.defaultSuccessUrl("http://localhost:3000/login/callback", true) // 로그인 성공 시 리다이렉트 url
					.failureUrl("/loginFailure")// 로그인 실패 시 리다이렉트 url
				// .authorizationEndpoint(authorization ->
				// 	authorization.baseUri("/oauth2/authorization")) // OAuth 인증 경로
			)
			.exceptionHandling(ex -> ex.authenticationEntryPoint(((request, response, authException) -> {
				response.setContentType("application/json; charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("{\"error\": \"Unauthorized - 액세스 토큰이 없거나 로그인 인증되지 않았습니다.\"}");
			})))
			.setSharedObject(HttpFirewall.class, relaxedHttpFirewall());
		return http.build();
	}

	@Bean
	public HttpFirewall relaxedHttpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedPercent(true); // % 인코딩 허용
		firewall.setAllowSemicolon(true); // 세미콜론 허용
		firewall.setAllowBackSlash(true); // 백슬래시 허용
		firewall.setAllowUrlEncodedSlash(true);  // URL 인코딩된 슬래시 허용
		firewall.setAllowUrlEncodedDoubleSlash(true); // URL 인코딩된 더블 슬래시 허용
		firewall.setAllowBackSlash(true); // 백슬래시 허용
		firewall.setAllowUrlEncodedPeriod(true); // . 허용
		firewall.setAllowedHostnames(hostname -> true); // 모든 호스트 허용 (기본적으로 검사 비활성화)
		return new DefaultHttpFirewall();
	}
}