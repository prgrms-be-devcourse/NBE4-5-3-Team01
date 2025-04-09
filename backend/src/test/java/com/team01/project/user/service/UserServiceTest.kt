package com.team01.project.user.service

import com.team01.project.domain.follow.repository.FollowRepository
import com.team01.project.domain.notification.service.NotificationService
import com.team01.project.domain.user.dto.UserDto
import com.team01.project.domain.user.entity.CalendarVisibility
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.RefreshTokenRepository
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.domain.user.service.UserService
import com.team01.project.global.security.JwtTokenProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @Mock
    lateinit var authorizedClientService: OAuth2AuthorizedClientService

    @Mock
    lateinit var notificationService: NotificationService

    @Mock
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    lateinit var followRepository: FollowRepository

//    @InjectMocks
//    lateinit var userService: UserService

    @Test
    fun testAddUser() {
        val clientId = "test-client-id"
        val clientSecret = "test-client-secret"
        val spotifyTokenUrl = "https://api.spotify.com/token"

        // 직접 UserService 인스턴스 생성
        val userService = UserService(
            userRepository = userRepository,
            refreshTokenRepository = refreshTokenRepository,
            authorizedClientService = authorizedClientService,
            followRepository = followRepository,
            passwordEncoder = passwordEncoder,
            notificationService = notificationService,
            jwtTokenProvider = jwtTokenProvider,
            clientId = clientId,
            clientSecret = clientSecret,
            spotifyTokenUrl = spotifyTokenUrl
        )

        val userDto = UserDto(
            id = "kmnj2100",
            password = "password",
            email = "test@example.com",
            name = "Test User",
            originalName = "Original Test User",
            userIntro = null,
            image = null,
            birthDay = null,
            createdDate = null,
            field = "Test Field",
            calendarVisibility = CalendarVisibility.PUBLIC
        )

        `when`(passwordEncoder.encode(anyString())).thenReturn("encodedPassword")

        val expectedUser = User(
            id = userDto.id,
            email = userDto.email,
            name = userDto.name,
            originalName = userDto.originalName,
            field = userDto.field,
            userPassword = "encodedPassword",
            createdDate = LocalDateTime.now(),
            birthDay = null,
            image = null,
            calendarVisibility = CalendarVisibility.PUBLIC,
            refreshTokens = null,
            notifications = mutableListOf()
        )

        `when`(userRepository.save(any(User::class.java))).thenReturn(expectedUser)

        val actualUser = userService.addUser(userDto)

        assertNotNull(actualUser)
        assertEquals(expectedUser.id, actualUser.id)
        assertEquals(expectedUser.email, actualUser.email)
        assertEquals(expectedUser.name, actualUser.name)
        assertEquals(expectedUser.originalName, actualUser.originalName)
        assertEquals(expectedUser.field, actualUser.field)
        assertEquals(expectedUser.userPassword, actualUser.userPassword)
    }
}