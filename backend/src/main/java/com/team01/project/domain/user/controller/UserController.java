package com.team01.project.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.team01.project.domain.user.repository.RefreshTokenRepository;
import com.team01.project.global.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/user")
@Controller
public class UserController {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@GetMapping("/login")
	public String loginPage(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			System.out.println("인증확인" + authentication);
			return "redirect:/"; // 이미 인증된 사용자는 메인 페이지로 리다이렉트
		}
		return "login"; // 로그인 페이지를 반환
	}

	@ResponseBody
	@PostMapping("/logout")
	public String logout(@RequestBody String userId) {
		refreshTokenRepository.deleteByUserId(userId);
		return "로그아웃 성공";
	}

	@GetMapping("/spotify/logout")
	public String forceLogout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		System.out.println("강제로그아웃");

		if (authentication != null) {
			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}
		;

		request.getSession().invalidate();
		return "redirect:https://accounts.spotify.com/en/logout";
	}

	@ResponseBody
	@PostMapping("/refresh")
	public String refreshToken(@RequestBody String refreshToken) {
		String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
		String newAccessToken = getNewSpotifyAccessToken(refreshToken);

		String newJwtToken = jwtTokenProvider.createToken(userId, newAccessToken);
		return newJwtToken;
	}

	private String getNewSpotifyAccessToken(String refreshToken) {
		return refreshToken;
	}
}