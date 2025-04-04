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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws ServletException, IOException {

		log.info("======= START JwtTokenFilter.doFilterInternal =======");

		String header = request.getHeader("Authorization");
		log.info("Header value:{}", header);

		String token = null;

		// Authorization 헤더가 존재하고 "Bearer "로 시작하면, 그 토큰을 사용
		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7); // "Bearer " 이후의 JWT 값 추출
		}

		// 헤더에 토큰이 없으면 쿠키에서 "access_token"을 찾아봅니다.
		if (token == null) {
			log.info("Authorization 헤더에 토큰이 없으므로 쿠키에서 토큰을 찾습니다.");
			if (request.getCookies() != null) {

				for (var cookie : request.getCookies()) {
					if ("accessToken".equals(cookie.getName())) {
						token = cookie.getValue();
						// log.info("쿠키 액세스 토큰:{} ", token);
						break;
					}
				}
			}
		}

		// 토큰이 여전히 없으면 필터 체인 계속 진행
		if (token == null) {
			log.info("토큰을 찾을 수 없습니다.");
			chain.doFilter(request, response);
			return;
		}

		// log.info("[JwtTokenFilter] 클라이언트에서 받은 JWT:{}", token);

		boolean isValid = jwtTokenProvider.validateToken(token);
		log.info("JWT 검증 결과:{} ", isValid);

		if (!isValid) {
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
		// log.info("SecurityContext에 설정된 인증 정보:{}", SecurityContextHolder.getContext().getAuthentication());

		chain.doFilter(request, response);
	}
}
