package com.team01.project.domain.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
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

import com.team01.project.domain.follow.controller.dto.FollowResponse;
import com.team01.project.domain.user.dto.SimpleUserResponse;
import com.team01.project.domain.user.dto.UserDto;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.RefreshTokenRepository;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.domain.user.service.SpotifyRefreshTokenService;
import com.team01.project.domain.user.service.UserService;
import com.team01.project.global.dto.RsData;
import com.team01.project.global.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Users", description = "유저 API")
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final SpotifyRefreshTokenService spotifyRefreshTokenService;
	private final UserService userService;
	private final UserRepository userRepository;

	@Operation(summary = "로그인 api", description = "db에서 아이디 조회 후 입력한 비밀번호 검증 후 토큰 반환")
	@ResponseBody
	@PostMapping("/login")
	public RsData<Map<String, Object>> login(@RequestBody Map<String, Object> reqMap) {

		return new RsData<>("200-1", "로그인 성공", userService.validLogin(reqMap));

	}

	// @GetMapping("/loginFailure")
	// public String loginFailure(Model model) {
	// 	model.addAttribute("error", "로그인 실패");
	// 	return "login";  // 로그인 실패 시 사용자에게 오류 메시지 표시
	// }

	//
	// @ResponseBody
	// @PostMapping("/logout")
	// public String logout(@RequestBody String userId) {
	// 	refreshTokenRepository.deleteByUserId(userId);
	// 	return "로그아웃 성공";
	// }

	// @GetMapping("/logout")
	// public void forceLogout(HttpServletRequest request, HttpServletResponse response,
	// 	Authentication authentication) throws IOException {
	// 	try {
	// 		userService.logoutService(request, response, authentication);
	// 		log.info("로그아웃 성공");
	// 		// 로그아웃 성공 후, 로그인 페이지로 명시적으로 리다이렉트
	// 		response.sendRedirect("http://localhost:3000/login");
	// 	} catch (Exception e) {
	// 		log.info("로그아웃 실패", e);
	// 		response.sendRedirect("/login?error=true");
	// 	}
	// }

	@GetMapping("/logout")
	public ResponseEntity<?> forceLogout(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		return userService.logoutService(request, response, authentication);
	}

	@Operation(summary = "jwt 재발급 api", description = "리프레시 토큰을 이용한 jwt를 재발급 한다.")
	@ResponseBody
	@PostMapping("/refresh")
	public RsData<Map<String, Object>> refreshToken(@RequestBody Map<String, Object> reqMap) {
		String refreshToken = reqMap.get("refreshToken").toString();
		Map<String, Object> rsToken = userService.refreshToken(refreshToken);
		return new RsData<>("200-1", "토큰을 재발급 합니다.", rsToken);
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
	//
	// @ResponseBody
	// @GetMapping("testApiCookie")
	// public ResponseEntity<?> logout(@CookieValue(name = "accessToken", required = false) String accessToken) {
	// 	System.out.println("쿠키" + accessToken);
	// 	// accessToken 값이 null이 아니라면 토큰 기반 로그아웃 로직 수행
	// 	// 예: 토큰 검증, 리프레시 토큰 삭제 등
	// 	// ...
	// 	return ResponseEntity.ok("Logged out");
	// }

	@Operation(summary = "유저 검색 api", description = "이름과 닉네임으로 유저를 검색한다.")
	@ResponseBody
	@GetMapping("/search")
	public RsData<List<FollowResponse>> search(
		@RequestParam(name = "q") String name,
		@AuthenticationPrincipal OAuth2User user
	) {
		return new RsData<>(
			"200-1",
			"유저 검색이 완료되었습니다.",
			userService.search(user.getName(), name)
		);
	}

	@Operation(summary = "유저 조회 api", description = "현재 로그인한 유저의 정보를 조회한다.")
	@ResponseBody
	@GetMapping("/byToken")
	public SimpleUserResponse getUserByToken(@RequestHeader("Authorization") String accessToken) {
		String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
		String userId = jwtTokenProvider.getUserIdFromToken(token);
		return SimpleUserResponse.from(userService.getUserById(userId));
	}

	@Operation(summary = "유저 정보 api", description = "현재 로그인한 유저의 정보를 가져온다.")
	@ResponseBody
	@GetMapping("/getUsers")
	public RsData<UserDto> getUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
		String userId = oAuth2User.getName();
		User user = userService.findByUserId(userId);
		return new RsData<>(
			"200-1",
			"유저 정보 조회 완료.",
			UserDto.from(user)
		);
	}

	@Operation(summary = "자기소개 변경 api", description = "현재 로그인한 유저의 자기소개를 수정한다.")
	@ResponseBody
	@PutMapping("/userIntro")
	public RsData<Void> userIntro(@AuthenticationPrincipal OAuth2User oAuth2User,
		@RequestBody Map<String, Object> reqMap) {
		String userIntro = reqMap.get("userIntro").toString();
		String userId = oAuth2User.getName();
		userService.updateUserIntro(userId, userIntro);

		return new RsData<>(
			"200-1",
			"user intro modified"
		);
	}

	@Operation(summary = "이름 변경 api", description = "현재 로그인한 유저의 이름을 수정한다.")
	@ResponseBody
	@PutMapping("/profileName")
	public RsData<Void> changeProfileName(
		@AuthenticationPrincipal OAuth2User oAuth2User,
		@RequestBody Map<String, Object> reqMap) {
		String profileName = reqMap.get("name").toString();
		String userId = oAuth2User.getName();
		userService.updateProfileName(userId, profileName);

		return new RsData<>(
			"200-1",
			"user name modified"
		);
	}

	@Operation(summary = "이미지 변경 api", description = "현재 로그인한 유저의 프로필 사진을 변경한다.")
	@ResponseBody
	@PostMapping("/image")
	public RsData<String> uploadImage(
		@AuthenticationPrincipal OAuth2User oAuth2User,
		@RequestParam("image") MultipartFile file) {
		String userId = oAuth2User.getName();
		// log.info("파일네임:{}", file.getName());
		try {
			String savedFileInfo = userService.uploadImage(userId, file);

			// 성공적으로 저장되었으면 200 OK와 함께 정보 반환
			return new RsData<>("200-1",
				"File uploaded successfully.",
				savedFileInfo);
		} catch (Exception e) {
			// 예외 발생 시 500 응답
			return new RsData<>("500",
				"error");
		}

	}

	@Operation(summary = "유저 조회 api", description = "아이디가 user-id인 유저의 정보를 조회한다.")
	@ResponseBody
	@GetMapping("/{user-id}")
	public SimpleUserResponse getUserByUserId(@PathVariable(name = "user-id") String userId) {
		return SimpleUserResponse.from(userService.getUserById(userId));
	}

	@Operation(summary = "유저 조회 api", description = "현재 로그인한 유저의 정보를 조회한다.")
	@ResponseBody
	@GetMapping("/byCookie")
	public SimpleUserResponse getUserByCookie(@CookieValue(name = "accessToken") String accessToken) {
		String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
		return SimpleUserResponse.from(userService.getUserById(userId));
	}

	@Operation(summary = "Spotify Token 반환", description = "쿠키에 존재하는 Spotify Token 반환한다.")
	@GetMapping("/spotify-token")
	public ResponseEntity<String> getSpotifyToken(@AuthenticationPrincipal OAuth2User user) {
		String spotifyToken = user.getAttribute("spotifyToken");
		return ResponseEntity.ok(spotifyToken);
	}

	@ResponseBody
	@Operation(summary = "회원가입 api", description = "회원가입 기능")
	@PostMapping("/signup")
	public RsData<UserDto> userSignUp(@RequestBody UserDto body) {
		User savedUser = userService.addUser(body);
		UserDto userDto = UserDto.from(savedUser);
		return new RsData<>("200-1", "등록된 유저", userDto);
	}

	@ResponseBody
	@Operation(summary = "아이디 중복 확인 api", description = "회원가입 아이디 중복 확인")
	@GetMapping("/check-duplicate")
	public RsData<Boolean> checkDuplicate(@RequestParam("checkId") String checkId) {

		boolean exists = userService.existsByLoginId(checkId);
		if (exists) {
			exists = false;
		} else {
			exists = true;
		}

		return new RsData<>("200-1", "아이디 중복 체크", exists);
	}
}