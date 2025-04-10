package com.team01.project.domain.user.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.team01.project.domain.user.entity.RefreshToken
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.RefreshTokenRepository
import com.team01.project.domain.user.repository.UserRepository
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class SpotifyLinkService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    private val log = LoggerFactory.getLogger(SpotifyLinkService::class.java)
    private val restTemplate = RestTemplate()

    fun getUserById(userId: String): User {
        return userRepository.findById(userId).orElseThrow {
            RuntimeException("해당 유저를 찾을 수 없습니다.")
        }
    }

    fun saveUser(user: User) {
        userRepository.save(user)
    }

    fun saveSpotifyToken(user: User, spotifyRefreshToken: String) {
        val tokenEntity = refreshTokenRepository.findByUser(user).orElse(
            RefreshToken(user = user, createdAt = LocalDateTime.now())
        )
        tokenEntity.spotifyRefreshToken = spotifyRefreshToken
        refreshTokenRepository.save(tokenEntity)
    }

    fun requestSpotifyTokens(
        code: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        tokenUri: String
    ): Map<String, String> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val body: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("code", code)
            add("redirect_uri", redirectUri)
            add("client_id", clientId)
            add("client_secret", clientSecret)
        }

        val request = HttpEntity(body, headers)
        val response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, String::class.java)

        if (response.statusCode != HttpStatus.OK) {
            log.error("Spotify 토큰 요청 실패: ${response.statusCode}")
            throw RuntimeException("Spotify 토큰 요청 실패")
        }

        val json: JsonNode = ObjectMapper().readTree(response.body)
        return mapOf(
            "access_token" to (json["access_token"]?.asText() ?: ""),
            "refresh_token" to (json["refresh_token"]?.asText() ?: "")
        )
    }

    fun logoutWithSpotifyContext(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ): ResponseEntity<String> {
        authentication?.let {
            val userId = it.name
            val user = userRepository.findById(userId).orElse(null)
            user?.let { u ->
                val loginPlatform = u.loginPlatform
                val tokenEntity = refreshTokenRepository.findByUser(u).orElse(null)

                if (tokenEntity != null) {
                    if (loginPlatform == "STAN-002") {
                        tokenEntity.refreshToken = null
                        refreshTokenRepository.save(tokenEntity)
                    } else {
                        refreshTokenRepository.delete(tokenEntity)
                    }
                }
            }
            SecurityContextLogoutHandler().logout(request, response, authentication)
        }

        request.session.invalidate()
        SecurityContextHolder.clearContext()

        val cookies = listOf("accessToken", "spotifyAccessToken", "refreshToken").map {
            Cookie(it, null).apply {
                path = "/"
                isHttpOnly = true
                maxAge = 0
            }
        }

        cookies.forEach { response.addCookie(it) }

        return ResponseEntity.status(200).body("로그아웃 성공")
    }
}
