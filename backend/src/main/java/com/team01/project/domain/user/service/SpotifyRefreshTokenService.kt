package com.team01.project.domain.user.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class SpotifyRefreshTokenService {

    @Value("\${spring.security.oauth2.client.registration.spotify.client-id}")
    lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.spotify.client-secret}")
    lateinit var clientSecret: String

    companion object {
        private const val TOKEN_URL = "https://accounts.spotify.com/api/token"
        private val log = LoggerFactory.getLogger(SpotifyRefreshTokenService::class.java)
    }

    private val restTemplate = RestTemplate()

    fun refreshAccessToken(refreshToken: String): String {
        log.info("===== START SpotifyRefreshTokenService.refreshAccessToken =====")

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            setBasicAuth(clientId, clientSecret) // 클라이언트 ID, 시크릿 인증
        }

        val body: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("code", refreshToken)
            add("redirect_uri", "http://localhost:3000/login/callback")
        }

        val requestEntity = HttpEntity(body, headers)

        // ResponseEntity<Map<String, Any>> 로 강제 형변환
        val response = restTemplate.exchange(
            TOKEN_URL,
            HttpMethod.POST,
            requestEntity,
            Map::class.java
        ) as ResponseEntity<Map<String, Any>>

        val responseBody = response.body
        if (responseBody != null && responseBody.containsKey("refresh_token")) {
            return responseBody["refresh_token"].toString()
        }

        throw RuntimeException("리프레시 토큰을 가져오지 못했습니다.")
    }
}
