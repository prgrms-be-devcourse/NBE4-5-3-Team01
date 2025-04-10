package com.team01.project.domain.user.service

import com.team01.project.domain.notification.service.NotificationService
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.RefreshTokenRepository
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.security.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Collections
import java.util.Optional

@Service
class CustomOAuth2UserService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
    private val notificationService: NotificationService
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private val delegate = DefaultOAuth2UserService()

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        log.info("======= START CustomOAuth2UserService.loadUser =======")

        // userRequest와 accessToken은 non-null 타입으로 선언되어 있음
        val spotifyAccessToken = userRequest.accessToken.tokenValue

        val oAuth2User: OAuth2User = delegate.loadUser(userRequest)
        val userId = oAuth2User.name ?: throw RuntimeException("OAuth2 사용자 ID를 찾을 수 없습니다.")
        log.info("OAuth2 User ID: {}", userId)

        var foundUser: User? = userRepository.findById(userId).orElse(null)

        // DB에 사용자 없을 시 새로 생성
        if (foundUser == null) {
            foundUser = User(
                id = userId,
                name = oAuth2User.getAttribute<String>("display_name") ?: "",
                email = oAuth2User.getAttribute<String>("email") ?: "",
                loginPlatform = "SPOT-001",
                createdDate = LocalDateTime.now()
            )

            userRepository.save(foundUser)
            log.info("최초 로그인 사용자 저장: {}", userId)

            notificationService.createDefaultNotifications(foundUser)
            log.info("{}님의 알림이 생성되었습니다.", foundUser.name)

            notificationService.initLoginNotifications(LocalTime.now(), foundUser)
        }

        // 기존에 동일한 유저 ID가 있으면 리프레시 토큰 삭제
        val matchId: Optional<User> = userRepository.findById(userId)
        if (matchId.isPresent) {
            log.info("리프레시 토큰 테이블에 동일한 유저 ID 있을 때 기존 리프레시 토큰 삭제")
            refreshTokenRepository.deleteByUserId(userId)
        }

        // JWT 발급
        val jwtToken = jwtTokenProvider.generateJwtToken(userId, spotifyAccessToken)
        val refreshToken = jwtTokenProvider.generateRefreshToken(userId)

        val attributes: MutableMap<String, Any> = HashMap(oAuth2User.attributes)
        attributes["spotifyToken"] = spotifyAccessToken
        attributes["jwtToken"] = jwtToken
        attributes["refreshToken"] = refreshToken

        log.info("유저서비스에서 생성된 jwt: {}", jwtToken)

        return DefaultOAuth2User(
            Collections.singleton(SimpleGrantedAuthority("USER")),
            attributes,
            "id"
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)
    }
}
