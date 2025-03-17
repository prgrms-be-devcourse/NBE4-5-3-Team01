package com.team01.project.domain.user.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team01.project.domain.follow.controller.dto.FollowResponse;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.user.entity.RefreshToken;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.RefreshTokenRepository;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;

	private final FollowRepository followRepository;

	@Value("${spring.security.oauth2.client.registration.spotify.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.spotify.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.provider.spotify.token-uri}")
	private String spotifyTokenUrl;

	private final String uploadDir = "uploads/profiles/";

	@Transactional
	public ResponseEntity<?> refreshToken(String refreshTokenValue) {
		System.out.println("======= START UserService.refreshToken =======");
		refreshTokenValue = refreshTokenValue.replace("\"", "");
		if (!jwtTokenProvider.validateToken(refreshTokenValue)) {

			Map<String, Object> response = new HashMap<>();
			response.put("status", 401);
			response.put("message", "리프레시 토큰 검증에 실패했습니다.");
			return ResponseEntity.status(401).body(response);
		}

		//현재 인증된 사용자의 정보를 얻음
		OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken)SecurityContextHolder.getContext()
			.getAuthentication();

		//사용자 아이디 얻음
		String userId = authenticationToken.getName();

		//사용자 찾기
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

		//리프레시 토큰 찾기
		RefreshToken storedRefreshToken = refreshTokenRepository.findByUser(user)
			.orElseThrow(() -> new RuntimeException("리프레시 토큰을 찾을 수 없습니다."));

		// 리프레시 토큰이 일치하는지 확인
		if (!storedRefreshToken.getRefreshToken().equals(refreshTokenValue)) {
			Map<String, Object> response = new HashMap<>();
			response.put("status", 401);
			response.put("message", "리프레시 토큰이 일치하지 않습니다.");
			return ResponseEntity.status(401).body(response);
		}

		//스포티파이 토큰 재발급
		OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken)SecurityContextHolder
			.getContext()
			.getAuthentication();

		//  스포티파이 액세스 토큰을 가져오기
		OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("spotify",
			oauth2AuthenticationToken.getName());

		if (authorizedClient == null) {
			Map<String, Object> response = new HashMap<>();
			response.put("status", 401);
			response.put("message", "사용자 인증에 실패하였습니다.");
			return ResponseEntity.status(401).body(response);
		}

		//스포티파이 액세스 토큰 재발급
		String spotifyAccessToken = refreshSpotifyAccessToken(authorizedClient);

		//서버 jwt 토큰 재발급
		String newAccessToken = jwtTokenProvider.generateJwtToken(user.getId(), spotifyAccessToken);

		Map<String, Object> response = new HashMap<>();
		response.put("status", 200);
		response.put("accessToken", newAccessToken);
		response.put("spotifyAccessToken", spotifyAccessToken);
		return ResponseEntity.status(200).body(response);
	}

	private String refreshSpotifyAccessToken(OAuth2AuthorizedClient authorizedClient) {
		// OAuth2AuthorizedClient에서 스포티파이 액세스 토큰을 얻는 방법
		OAuth2RefreshToken oAuth2RefreshToken = authorizedClient.getRefreshToken();

		if (oAuth2RefreshToken == null) {
			throw new RuntimeException("스포티파이의 리프레시 토큰을 찾을 수 없습니다.");
		}

		// 스포티파이 API에 요청을 보내기 위한 파라미터 설정
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "refresh_token");
		body.add("refresh_token", oAuth2RefreshToken.getTokenValue()); // 리프레시 토큰
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);

		// 요청 헤더 설정 (Content-Type: application/x-www-form-urlencoded)
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// 요청 객체
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

		// RestTemplate을 사용하여 POST 요청 보내기
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(spotifyTokenUrl, HttpMethod.POST, request,
			String.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			String responseBody = response.getBody();
			return extractAccessTokenResponse(responseBody); // 실제 구현에서 반환되는 스포티파이 액세스 토큰
		} else {
			throw new RuntimeException("스포티파이의 액세스 토큰을 재발급 받지 못했습니다.");
		}
	}

	// JSON 응답에서 액세스토큰 추출
	private String extractAccessTokenResponse(String responseBody) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(responseBody);
			return jsonNode.get("access_token").asText();
		} catch (Exception e) {
			throw new RuntimeException("json 응답을 받는데 실패했습니다.", e);
		}
	}

	public List<FollowResponse> search(String currentUserId, String name) {
		User currentUser = userRepository.getById(currentUserId);
		List<User> users = userRepository.searchUser(name);;

		return users.stream()
			.filter(user -> !user.getId().equals(currentUser.getId()))
			.map(user ->
				FollowResponse.of(
				user,
				checkFollow(user, currentUser),
				checkFollow(currentUser, user)
			))
			.toList();
	}

	public User getUserById(String id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저 찾을 수 없습니다: " + id));
	}

	private boolean checkFollow(User user, User currentUser) {
		return followRepository.existsByToUserAndFromUser(user, currentUser);
	}

	public User findByUserId(String userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("유저의 ID를 찾을 수 없습니다. " + userId));
	}

	@Transactional
	public void updateUserIntro(String userId, String userIntro) {
		// 기존 엔티티를 조회
		User existingUser = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		// toBuilder()를 사용하여 기존 값을 복사한 뒤, userIntro 필드만 변경
		User updatedUser = existingUser.toBuilder()
			.userIntro(userIntro)
			.build();

		userRepository.save(updatedUser);
	}

	@Transactional
	public void updateProfileName(String userId, String profileName) {
		// 기존 엔티티를 조회
		User existingUser = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		// toBuilder()를 사용하여 기존 값을 복사한 뒤, userIntro 필드만 변경
		User updatedUser = existingUser.toBuilder()
			.name(profileName)
			.build();

		userRepository.save(updatedUser);
	}

	@Transactional
	public String uploadImage(String userId, MultipartFile file) {
		// 1. 기존 User 조회
		User existingUser = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		try {
			// 2. MultipartFile -> byte[] -> Base64 인코딩
			byte[] fileBytes = file.getBytes();
			String base64Image = Base64.getEncoder().encodeToString(fileBytes);

			// 3. toBuilder()로 기존 User 복사, image 필드만 교체
			User updatedUser = existingUser.toBuilder()
				.image(base64Image)
				.build();

			// 4. 저장
			userRepository.save(updatedUser);

			return "Image updated for user: " + userId;

		} catch (Exception e) {
			throw new RuntimeException("Failed to read file bytes", e);
		}
	}

	// @Transactional
	// public String uploadImage(String userId, MultipartFile file) {
	// 	User existingUser = userRepository.findById(userId)
	// 		.orElseThrow(() -> new RuntimeException("User not found"));
	//
	// 	try {
	// 		// 1. 파일 이름 생성 (충돌 방지를 위해 UUID 사용)
	// 		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
	// 		String filePath = uploadDir + fileName;
	//
	// 		// 2. 실제 파일 저장 경로 생성
	// 		Path uploadPath = Paths.get(uploadDir);
	// 		if (!Files.exists(uploadPath)) {
	// 			Files.createDirectories(uploadPath);
	// 		}
	//
	// 		// 3. 파일 저장
	// 		Path targetLocation = uploadPath.resolve(fileName);
	// 		Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
	//
	// 		// 4. DB에는 파일 경로만 저장
	// 		User updatedUser = existingUser.toBuilder()
	// 			.image("/api/images/" + fileName) // 클라이언트에서 접근할 URL 경로
	// 			.build();
	//
	// 		userRepository.save(updatedUser);
	//
	// 		return "Image updated for user: " + userId;
	//
	// 	} catch (IOException e) {
	// 		throw new RuntimeException("Failed to store file", e);
	// 	}
	// }
}
