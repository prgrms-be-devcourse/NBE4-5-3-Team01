package com.team01.project.domain.user.controller

import com.team01.project.domain.user.service.SpotifyLinkService
import com.team01.project.global.dto.RsData
import com.team01.project.global.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/spotify")
class SpotifyConnectController(
    private val spotifyLinkService: SpotifyLinkService,
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${spring.security.oauth2.client.registration.spotify.client-id}")
    private val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.spotify.client-secret}")
    private val clientSecret: String,
    @Value("\${spring.security.oauth2.client.provider.spotify.token-uri}")
    private val tokenUri: String
) {

    private val log = LoggerFactory.getLogger(SpotifyConnectController::class.java)

    @GetMapping("/callback")
    fun connectSpotify(
        @RequestParam("code") code: String,
        @RequestParam("state") jwt: String,
        response: HttpServletResponse
    ): RsData<Map<String, String>> {
        val userId = jwtTokenProvider.getUserIdFromToken(jwt)
        val user = spotifyLinkService.getUserById(userId)

        val accessTokenResponse = spotifyLinkService.requestSpotifyTokens(
            code = code,
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = "http://localhost:3000/user/spotify-callback",
            tokenUri = tokenUri
        )

        val accessToken = accessTokenResponse["access_token"]
        val refreshToken = accessTokenResponse["refresh_token"]

        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            log.error("Spotify 토큰 파싱 실패")
            return RsData("500", "Spotify 토큰 파싱 실패", emptyMap())
        }

        spotifyLinkService.saveSpotifyToken(user, refreshToken)

        if (user.loginPlatform == "STAN-001") {
            user.loginPlatform = "STAN-002"
            spotifyLinkService.saveUser(user)
        }

        val newJwt = jwtTokenProvider.generateJwtToken(userId, accessToken)

        val encodedJwt = URLEncoder.encode(newJwt, StandardCharsets.UTF_8)
        val encodedSpotify = URLEncoder.encode(accessToken, StandardCharsets.UTF_8)

        response.setHeader("Set-Cookie", "accessToken=$encodedJwt; Path=/; HttpOnly")
        response.addHeader("Set-Cookie", "spotifyAccessToken=$encodedSpotify; Path=/; HttpOnly")

        return RsData("200", "Spotify 연동 완료", mapOf("redirectUrl" to "http://localhost:3000/user/profile"))
    }

    @GetMapping("/logout")
    fun spotifyAwareLogout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ): ResponseEntity<*> {
        return spotifyLinkService.logoutWithSpotifyContext(request, response, authentication)
    }
}
