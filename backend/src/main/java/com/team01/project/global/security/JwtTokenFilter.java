package com.team01.project.global.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws
		ServletException, IOException {

		System.out.println("======= START JwtTokenFilter.doFilterInternal =======");

		String header = request.getHeader("Authorization");
		System.out.println("header값:" + header);

		// Authorization 헤더 확인 (Bearer 토큰 여부)
		if (header == null || !header.startsWith("Bearer ")) {
			System.out.println("Authorization 헤더가 없거나 잘못됨");
			chain.doFilter(request, response);
			return;
		}

		String token = header.substring(7); //"Bearer" 이후의 JWT 값 추출
		System.out.println("[JwtTokenFilter] 클라이언트에서 받은 JWT: " + token);

		boolean isValid = jwtTokenProvider.validateToken(token);
		System.out.println("JWT 검증 결과: " + isValid);

		if (!jwtTokenProvider.validateToken(token)) {
			response.setContentType("application/json; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("{\"error\": \"jwt token이 검증되지 않음\"}");
			return;
		}

		String spotifyToken = jwtTokenProvider.extractSpotifyToken(token);
		spotifyToken = (spotifyToken != null) ? spotifyToken : "";

		String userId = jwtTokenProvider.getUserIdFromToken(token);

		// 사용자 정보를 가져와서 인증 객체 생성
		OAuth2User oAuth2User = new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
			Map.of("id", userId, "spotifyToken", spotifyToken), // OAuth2User의 속성
			"id" // 기본 속성 키
		);

		// OAuth2User 기반으로 SecurityContext에 저장
		Authentication auth = new OAuth2AuthenticationToken(
			oAuth2User,
			oAuth2User.getAuthorities(),
			"spotify" // OAuth2 로그인 제공자명
		);

		// SecurityContext에 인증 정보 저장
		SecurityContextHolder.getContext().setAuthentication(auth);
		System.out.println("SecurityContext에 설정된 인증 정보: " + SecurityContextHolder.getContext().getAuthentication());

		chain.doFilter(request, response);
	}
}
