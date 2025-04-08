package com.team01.project.global.security

import com.team01.project.domain.user.entity.RefreshToken
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.RefreshTokenRepository
import com.team01.project.domain.user.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.LocalDateTime

@Component
class OAuth2SuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository
) : SimpleUrlAuthenticationSuccessHandler() {

    companion object {
        private val log = LoggerFactory.getLogger(OAuth2SuccessHandler::class.java)
    }

    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        log.info("======= START OAuth2SuccessHandler.onAuthenticationSuccess =======")

        // OAuth2 인증된 사용자 정보 가져오기
        val oAuth2User = authentication.principal as OAuth2User
        val userId: String? = oAuth2User.getAttribute("id")
        val refreshTokenValue: String? = oAuth2User.getAttribute("refreshToken")
        var spotifyRefreshTokenValue = ""

        if (userId == null) {
            throw RuntimeException("OAuth2 사용자 ID를 찾을 수 없습니다.")
        }

        if (authentication is OAuth2AuthenticationToken) {
            val userId2 = authentication.name

            val authorizedClient: OAuth2AuthorizedClient? =
                authorizedClientService.loadAuthorizedClient(authentication.authorizedClientRegistrationId, userId2)

            if (authorizedClient == null) {
                log.info("authorizedClient이 저장되지 않음 수동 저장 진행")
            } else {
                spotifyRefreshTokenValue = authorizedClient.refreshToken?.tokenValue ?: ""
            }

            // 데이터베이스에서 해당 사용자 조회 (없으면 null 반환)
            val foundUser: User? = userRepository.findById(userId2).orElse(null)
            // RefreshToken 객체 생성 (빌더를 사용하는 경우)
            val refreshToken = RefreshToken(
                user = foundUser,
                refreshToken = refreshTokenValue,
                spotifyRefreshToken = spotifyRefreshTokenValue,
                createdAt = LocalDateTime.now()
            )

            log.info("리프레시 토큰을 저장합니다.")
            refreshTokenRepository.save(refreshToken)
        }

        // Spotify Access Token 가져오기
        val spotifyAccessToken: String? = oAuth2User.getAttribute("spotifyToken")
        if (spotifyAccessToken == null || userId == null) {
            log.info("Spotify Access Token 또는 User ID 없음")
            response.sendRedirect("http://localhost:3000/login?error=token_not_found")
            return
        }
        val jwtToken: String? = oAuth2User.getAttribute("jwtToken")

        // 프론트엔드로 리다이렉트할 URL 생성
        val redirectUrl = "http://localhost:3000/login/callback" +
            "?access_token=" + jwtToken + "&refresh_token=" + refreshTokenValue +
            "&spotify_access_token=" + spotifyAccessToken

        response.sendRedirect(redirectUrl)
    }
}
