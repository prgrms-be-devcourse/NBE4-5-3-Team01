package com.team01.project.domain.user.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.team01.project.domain.follow.controller.dto.FollowResponse
import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.follow.repository.FollowRepository
import com.team01.project.domain.notification.service.NotificationService
import com.team01.project.domain.user.dto.UserDto
import com.team01.project.domain.user.entity.CalendarVisibility
import com.team01.project.domain.user.entity.RefreshToken
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.RefreshTokenRepository
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.security.JwtTokenProvider
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.Base64

@Service
class UserService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val followRepository: FollowRepository,
    private val passwordEncoder: PasswordEncoder,
    private val notificationService: NotificationService,
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${spring.security.oauth2.client.registration.spotify.client-id}")
    private val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.spotify.client-secret}")
    private val clientSecret: String,
    @Value("\${spring.security.oauth2.client.provider.spotify.token-uri}")
    private val spotifyTokenUrl: String
) {

    private val uploadDir = "uploads/profiles/"

    @Transactional
    fun refreshToken(refreshTokenValue: String): Map<String, Any>? {
        log.info("======= START UserService.refreshToken =======")
        var token = refreshTokenValue.replace("\"", "")
        if (!jwtTokenProvider.validateToken(token)) {
            val response = mutableMapOf<String, Any>(
                "status" to 401,
                "message" to "리프레시 토큰 검증에 실패했습니다."
            )
            return null
        }

        val authenticationToken =
            SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val userId = authenticationToken.name

        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("해당 유저를 찾을 수 없습니다.") }

        val storedRefreshToken = refreshTokenRepository.findByUser(user)
            .orElseThrow { RuntimeException("리프레시 토큰을 찾을 수 없습니다.") }

        if (storedRefreshToken.refreshToken != token) {
            val response = mutableMapOf<String, Any>(
                "status" to 401,
                "message" to "리프레시 토큰이 일치하지 않습니다."
            )
            return null
        }

        val authorizedClient = authorizedClientService
            .loadAuthorizedClient<OAuth2AuthorizedClient>("spotify", authenticationToken.name)

        if (authorizedClient == null) {
            val response = mutableMapOf<String, Any>(
                "status" to 401,
                "message" to "사용자 인증에 실패하였습니다."
            )
            return null
        }

        val spotifyAccessToken = refreshSpotifyAccessToken(authorizedClient)
        val newAccessToken = jwtTokenProvider.generateJwtToken(user.id, spotifyAccessToken)

        return mapOf(
            "status" to 200,
            "accessToken" to newAccessToken,
            "spotifyAccessToken" to spotifyAccessToken
        )
    }

    private fun refreshSpotifyAccessToken(authorizedClient: OAuth2AuthorizedClient): String {
        val oAuth2RefreshToken: OAuth2RefreshToken = authorizedClient.refreshToken
            ?: throw RuntimeException("스포티파이의 리프레시 토큰을 찾을 수 없습니다.")

        val body: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "refresh_token")
            add("refresh_token", oAuth2RefreshToken.tokenValue)
            add("client_id", clientId)
            add("client_secret", clientSecret)
        }

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val request: HttpEntity<MultiValueMap<String, String>> = HttpEntity(body, headers)
        val restTemplate = RestTemplate()
        val response: ResponseEntity<String> = restTemplate.exchange(
            spotifyTokenUrl, HttpMethod.POST, request, String::class.java
        )

        return if (response.statusCode == HttpStatus.OK) {
            val responseBody = response.body
                ?: throw RuntimeException("스포티파이 응답 본문이 비어 있습니다.")
            extractAccessTokenResponse(responseBody)
        } else {
            throw RuntimeException("스포티파이의 액세스 토큰을 재발급 받지 못했습니다.")
        }
    }

    private fun extractAccessTokenResponse(responseBody: String): String {
        return try {
            val objectMapper = ObjectMapper()
            val jsonNode: JsonNode = objectMapper.readTree(responseBody)
            jsonNode.get("access_token").asText()
        } catch (e: Exception) {
            throw RuntimeException("json 응답을 받는데 실패했습니다.", e)
        }
    }

    fun search(currentUserId: String, name: String): List<FollowResponse> {
        val currentUser = userRepository.getById(currentUserId)
        val users = userRepository.searchUser(name)
        return users.filter { it.id != currentUser.id }
            .map { FollowResponse.of(it, checkFollow(it, currentUser), checkFollow(currentUser, it)) }
    }

    fun getUserById(id: String): User {
        return userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 ID의 유저 찾을 수 없습니다: $id") }
    }

    private fun checkFollow(user: User, currentUser: User): Status {
        return followRepository.findStatusByToUserAndFromUser(user, currentUser).orElse(Status.NONE)
    }

    @Transactional
    fun findByUserId(userId: String): User {
        return userRepository.findById(userId)
            .orElseThrow { RuntimeException("유저의 ID를 찾을 수 없습니다. $userId") }
    }

    @Transactional
    fun updateUserIntro(userId: String, userIntro: String) {
        val existingUser = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        existingUser.userIntro = userIntro
        userRepository.save(existingUser)
    }

    @Transactional
    fun updateProfileName(userId: String, profileName: String) {
        val existingUser = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        existingUser.name = profileName
        userRepository.save(existingUser)
    }

    @Transactional
    fun uploadImage(userId: String, file: MultipartFile): String {
        val existingUser = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        return try {
            val fileBytes = file.bytes
            val base64Image = Base64.getEncoder().encodeToString(fileBytes)
            existingUser.image = base64Image
            userRepository.save(existingUser)
            "Image updated for user: $userId"
        } catch (e: Exception) {
            throw RuntimeException("Failed to read file bytes", e)
        }
    }

    @Transactional
    fun updateCalendarVisibility(userId: String, newCalendarVisibility: CalendarVisibility) {
        val user = userRepository.getById(userId)
        user.updateCalendarVisibility(newCalendarVisibility)
    }

    fun logoutService(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ): ResponseEntity<*> {
        if (authentication == null) {
            log.info("authentication 객체가 NULL입니다. SecurityContext에 인증 정보 없음.")
        }
        log.info("로그아웃 된 유저 ID: ${authentication?.name}")
        notificationService.deleteSubscription(authentication?.name ?: "")

        if (authentication is OAuth2AuthenticationToken) {
            val oAuth2User: OAuth2User = authentication.principal
            val userId: String? = oAuth2User.getAttribute("id")
            if (userId != null) {
                log.info("저장된 RefreshToken 삭제: $userId")
                refreshTokenRepository.deleteByUserId(userId)
            }
            SecurityContextLogoutHandler().logout(request, response, authentication)
        }

        request.session.invalidate()
        SecurityContextHolder.clearContext()
        log.info("SecurityContext 초기화 완료")

        val accessTokenCookie = Cookie("accessToken", null).apply {
            path = "/"
            isHttpOnly = true
            maxAge = 0
        }
        response.addCookie(accessTokenCookie)

        val spotifyAccessTokenCookie = Cookie("spotifyAccessToken", null).apply {
            path = "/"
            isHttpOnly = true
            maxAge = 0
        }
        response.addCookie(spotifyAccessTokenCookie)

        val refreshTokenCookie = Cookie("refreshToken", null).apply {
            path = "/"
            isHttpOnly = true
            maxAge = 0
        }
        response.addCookie(refreshTokenCookie)

        return ResponseEntity.status(200).body("로그아웃 성공")
    }

    @Transactional
    fun addUser(userDto: UserDto): User {
        val encodedPassword = passwordEncoder.encode(userDto.password)
        val user = User(
            id = userDto.id ?: throw IllegalArgumentException("User id is required"),
            email = userDto.email,
            name = userDto.name,
            originalName = userDto.originalName,
            field = userDto.field,
            userPassword = encodedPassword,
            createdDate = LocalDateTime.now()
            // 나머지 필드는 기본값 사용
        )
        return userRepository.save(user)
    }

    @Transactional
    fun existsByLoginId(id: String): Boolean {
        return userRepository.existsById(id)
    }

    fun validLogin(reqMap: Map<String, Any>): Map<String, Any>? {
        val loginId = reqMap["loginId"].toString()
        val userOpt = userRepository.findById(loginId)
        val password = userOpt.map { it.userPassword }.orElse(null)
        val isPasswordCorrect = passwordEncoder.matches(reqMap["password"].toString(), password ?: "")
        if (!isPasswordCorrect) {
            return null
        }
        val jwtToken = jwtTokenProvider.generateJwtToken(loginId, "")
        val refreshToken = jwtTokenProvider.generateRefreshToken(loginId)
        val foundUser = userRepository.findById(loginId).orElse(null)
        val saveRefreshToken = RefreshToken(
            user = foundUser,
            refreshToken = refreshToken,
            createdAt = LocalDateTime.now()
        )
        refreshTokenRepository.save(saveRefreshToken)
        return mapOf(
            "access_token" to jwtToken,
            "refresh_token" to refreshToken
        )
    }

    companion object {
        private val log = org.slf4j.LoggerFactory.getLogger(UserService::class.java)
    }
}
