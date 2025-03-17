package com.team01.project.domain.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.team01.project.domain.user.dto.SimpleUserResponse;
import com.team01.project.domain.user.dto.UserDto;
import com.team01.project.domain.user.repository.RefreshTokenRepository;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.domain.user.service.SpotifyRefreshTokenService;
import com.team01.project.domain.user.service.UserService;
import com.team01.project.global.security.JwtTokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final SpotifyRefreshTokenService spotifyRefreshTokenService;
	private final UserService userService;
	private final UserRepository userRepository;

	@GetMapping("/login")
	public String loginPage(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			System.out.println("인증확인:" + authentication);
			System.out.println("redirect front");
			return "redirect:http://localhost:3000"; // 이미 인증된 사용자는 메인 페이지로 리다이렉트
		}
		return "redirect:http://localhost:3000/login"; // 로그인 페이지를 반환
	}

	@GetMapping("/loginFailure")
	public String loginFailure(Model model) {
		model.addAttribute("error", "로그인 실패");
		return "login";  // 로그인 실패 시 사용자에게 오류 메시지 표시
	}
	//
	// @ResponseBody
	// @PostMapping("/logout")
	// public String logout(@RequestBody String userId) {
	// 	refreshTokenRepository.deleteByUserId(userId);
	// 	return "로그아웃 성공";
	// }

	@Transactional
	@GetMapping("/logout")
	public ResponseEntity<?> forceLogout(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {

		if (authentication == null) {
			System.out.println("authentication 객체가 NULL입니다. SecurityContext에 인증 정보 없음.");
			return ResponseEntity.status(403).body("authentication null"); // 프론트에서 토큰 삭제해야 함
		}

		System.out.println("로그아웃 된 유저 ID: " + authentication.getName());

		if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
			OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
			String userId = oAuth2User.getAttribute("id");

			if (userId != null) {
				System.out.println("저장된 RefreshToken 삭제: " + userId);
				refreshTokenRepository.deleteByUserId(userId); // 로그아웃 시 리프레시 토큰 삭제
			}

			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}

		request.getSession().invalidate();
		SecurityContextHolder.clearContext(); //SecurityContext 명시적으로 초기화
		System.out.println("SecurityContext 초기화 완료");

		// accessToken 쿠키 만료 처리
		Cookie accessTokenCookie = new Cookie("accessToken", null);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setHttpOnly(true);
		accessTokenCookie.setMaxAge(0); // 즉시 만료
		response.addCookie(accessTokenCookie);

		// spotifyAccessToken 쿠키 만료 처리
		Cookie spotifyAccessTokenCookie = new Cookie("spotifyAccessToken", null);
		spotifyAccessTokenCookie.setPath("/");
		spotifyAccessTokenCookie.setHttpOnly(true);
		spotifyAccessTokenCookie.setMaxAge(0); // 즉시 만료
		response.addCookie(spotifyAccessTokenCookie);

		return ResponseEntity.status(200).body("로그아웃 성공");
	}

	@ResponseBody
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody Map<String, Object> reqMap) {
		String refreshToken = reqMap.get("refreshToken").toString();
		return userService.refreshToken(refreshToken);
	}

	@ResponseBody
	@GetMapping("testApi")
	public Map<String, String> testApi(@AuthenticationPrincipal OAuth2User user) {

		String spotifyToken = user.getAttribute("spotifyToken");
		System.out.println("스포티파이 토큰체크:" + spotifyToken);
		String userId = user.getName();
		System.out.println("유저아이디 체크:" + userId);
		Map<String, String> resMap = new HashMap<>();
		resMap.put("res", "테스트 api 입니다.");
		resMap.put("userId", userId);
		return resMap;
	}

	@ResponseBody
	@GetMapping("testApiCookie")
	public ResponseEntity<?> logout(@CookieValue(name = "accessToken", required = false) String accessToken) {
		System.out.println("쿠키" + accessToken);
		// accessToken 값이 null이 아니라면 토큰 기반 로그아웃 로직 수행
		// 예: 토큰 검증, 리프레시 토큰 삭제 등
		// ...
		return ResponseEntity.ok("Logged out");
	}

	@ResponseBody
	@GetMapping("/search")
	public List<SimpleUserResponse> search(@RequestParam(name = "q") String name) {
		return userService.search(name).stream()
			.map(SimpleUserResponse::from)
			.toList();
	}

	@ResponseBody
	@GetMapping("/byToken")
	public SimpleUserResponse getUserByToken(@RequestHeader("Authorization") String accessToken) {
		String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
		String userId = jwtTokenProvider.getUserIdFromToken(token);
		return SimpleUserResponse.from(userService.getUserById(userId));
	}

	@ResponseBody
	@GetMapping("/getUsers")
	public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
		String userId = oAuth2User.getName();
		UserDto userDto = UserDto.from(userService.findByUserId(userId));
		return ResponseEntity.ok(userDto);
	}

	@ResponseBody
	@PutMapping("/userIntro")
	public void userIntro(@AuthenticationPrincipal OAuth2User oAuth2User, @RequestBody Map<String, Object> reqMap) {
		String userIntro = reqMap.get("userIntro").toString();
		String userId = oAuth2User.getName();
		userService.updateUserIntro(userId, userIntro);
	}

	@ResponseBody
	@PutMapping("/profileName")
	public void changeProfileName(@AuthenticationPrincipal OAuth2User oAuth2User,
		@RequestBody Map<String, Object> reqMap) {
		String profileName = reqMap.get("name").toString();
		String userId = oAuth2User.getName();
		userService.updateProfileName(userId, profileName);
	}

	@ResponseBody
	@PostMapping("/image")
	public ResponseEntity<?> uploadImage(@AuthenticationPrincipal OAuth2User oAuth2User,
		@RequestParam("image") MultipartFile file) {
		String userId = oAuth2User.getName();
		System.out.println("파일네임" + file.getName());
		try {
			String savedFileInfo = userService.uploadImage(userId, file);

			// 성공적으로 저장되었으면 200 OK와 함께 정보 반환
			return ResponseEntity.ok("File uploaded successfully. Info: " + savedFileInfo);
		} catch (Exception e) {
			// 예외 발생 시 500 응답
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("File upload failed: " + e.getMessage());
		}

	}

	@ResponseBody
	@GetMapping("/{user-id}")
	public SimpleUserResponse getUserByUserId(@PathVariable(name = "user-id") String userId) {
		return SimpleUserResponse.from(userService.getUserById(userId));
	}

	@ResponseBody
	@GetMapping("/byCookie")
	public SimpleUserResponse getUserByCookie(@CookieValue(name = "accessToken") String accessToken) {
		String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
		return SimpleUserResponse.from(userService.getUserById(userId));
	}

	@GetMapping("/spotify-token")
	public ResponseEntity<String> getSpotifyToken(@AuthenticationPrincipal OAuth2User user) {
		String spotifyToken = user.getAttribute("spotifyToken");
		return ResponseEntity.ok(spotifyToken);
	}
}