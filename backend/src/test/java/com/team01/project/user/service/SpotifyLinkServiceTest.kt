package com.team01.project.user.service

import com.team01.project.domain.user.dto.SpotifyTokenResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.client.ExpectedCount.once
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate

class SpotifyLinkServiceTest {

    private lateinit var spotifyLinkService: SpotifyLinkService
    private lateinit var mockServer: MockRestServiceServer
    private lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun setUp() {
        // 테스트용 더미 값으로 서비스 인스턴스 생성
        spotifyLinkService = SpotifyLinkService(
            clientId = "dummy-client-id",
            clientSecret = "dummy-client-secret",
            redirectUri = "http://localhost:8080/callback",
            userInfoUri = "https://api.spotify.com/v1/me"
        )

        // 서비스 내부에 있는 RestTemplate을 ReflectionTestUtils를 통해 가져옵니다.
        restTemplate = ReflectionTestUtils.getField(spotifyLinkService, "restTemplate") as RestTemplate

        // 해당 RestTemplate에 대해 MockRestServiceServer를 생성합니다.
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `exchangeCodeForToken returns valid token response`() {
        val dummyCode = "dummy-code"

        // token endpoint에 대한 모의 응답 JSON
        val tokenResponseJson = """
            {
                "access_token": "test-access-token",
                "token_type": "Bearer",
                "expires_in": 3600,
                "refresh_token": "test-refresh-token",
                "scope": "user-read-email"
            }
        """.trimIndent()

        // /v1/me endpoint에 대한 모의 응답 JSON (Spotify 사용자 ID를 조회)
        val userResponseJson = """
            {
                "id": "spotify-user-id"
            }
        """.trimIndent()

        // 1. token endpoint POST 요청 모의 설정
        mockServer.expect(once(), requestTo("https://accounts.spotify.com/api/token"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(tokenResponseJson, MediaType.APPLICATION_JSON))

        // 2. /v1/me endpoint GET 요청 모의 설정
        mockServer.expect(once(), requestTo("https://api.spotify.com/v1/me"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(userResponseJson, MediaType.APPLICATION_JSON))

        // 테스트 대상 메서드 호출
        val result: SpotifyTokenResponse? = spotifyLinkService.exchangeCodeForToken(dummyCode)

        // 모든 모의 요청이 충족되었는지 검증
        mockServer.verify()

        // 결과 검증: null이 아니고, 예상한 값들이 반환되어야 함
        assertNotNull(result)
        result?.let {
            assertEquals("test-access-token", it.accessToken)
            assertEquals("Bearer", it.tokenType)
            assertEquals(3600, it.expiresIn)
            assertEquals("test-refresh-token", it.refreshToken)
            assertEquals("user-read-email", it.scope)
            assertEquals("spotify-user-id", it.providerUserId)
        }
    }
}
