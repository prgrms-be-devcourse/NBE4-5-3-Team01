// package com.team01.project.user.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.BDDMockito.*;
//
// import java.util.Map;
// import java.util.Optional;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
// import org.springframework.security.oauth2.core.OAuth2AccessToken;
// import org.springframework.security.oauth2.core.user.OAuth2User;
//
// import com.team01.project.domain.user.entity.RefreshToken;
// import com.team01.project.domain.user.entity.User;
// import com.team01.project.domain.user.repository.RefreshTokenRepository;
// import com.team01.project.domain.user.repository.UserRepository;
// import com.team01.project.domain.user.service.CustomOAuth2UserService;
// import com.team01.project.global.security.JwtTokenProvider;
//
// @ExtendWith(MockitoExtension.class)
// public class CustomOAuth2UserServiceTest {
//
// 	@InjectMocks
// 	private CustomOAuth2UserService customOAuth2UserService;
//
// 	@Mock
// 	private UserRepository userRepository;
//
// 	@Mock
// 	private RefreshTokenRepository refreshTokenRepository;
//
// 	@Mock
// 	private JwtTokenProvider jwtTokenProvider;
//
// 	@Mock
// 	private OAuth2UserRequest userRequest;
//
// 	@Mock
// 	private OAuth2User oauth2User;
//
// 	@Mock
// 	private OAuth2AccessToken oAuth2AccessToken;
//
// 	@BeforeEach
// 	void setUp() {
// 		when(userRequest.getAccessToken()).thenReturn(oAuth2AccessToken);
// 		when(oAuth2AccessToken.getTokenValue()).thenReturn("mockAccessToken");
// 	}
//
// 	@Test
// 	void testLoadUser_success() {
//
// 		//테스트 데이터
// 		String userId = "asdf1234";
// 		String accessToken = "spotifyAccessToken";
// 		User mockUser = User.builder()
// 			.id(userId)
// 			.email("test@example.com")
// 			.build();
//
// 		// OAuth2User가 반환할 속성 설정
// 		Map<String, Object> attributes = Map.of("id", userId);
// 		given(oauth2User.getAttributes()).willReturn(attributes);
// 		given(oauth2User.getName()).willReturn(userId);
// 		given(userRequest.getAccessToken().getTokenValue()).willReturn(accessToken);
// 		given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
// 		given(jwtTokenProvider.generateJwtToken(userId, accessToken)).willReturn("mockJwtToken");
//
// 		// When (테스트 실행)
// 		OAuth2User result = customOAuth2UserService.loadUser(userRequest);
//
// 		// Then (검증)
// 		assertNotNull(result);
// 		assertEquals(userId, result.getName());
// 		verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
// 		verify(jwtTokenProvider, times(1)).generateJwtToken(userId, accessToken);
// 	}
// }
