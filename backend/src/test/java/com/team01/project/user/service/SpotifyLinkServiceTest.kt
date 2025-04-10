package com.team01.project.domain.user.service

import com.team01.project.domain.user.entity.RefreshToken
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.RefreshTokenRepository
import com.team01.project.domain.user.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime

class SpotifyLinkServiceTest {

    private lateinit var spotifyLinkService: SpotifyLinkService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        spotifyLinkService = SpotifyLinkService(userRepository, refreshTokenRepository)

        user = User(
            id = "test",
            email = "test@example.com",
            name = "Test User",
            createdDate = LocalDateTime.now(),
            userPassword = "password",
            loginPlatform = "STAN-001"
        )
    }

    @Test
    fun `saveSpotifyToken - saves new token when token not exists`() {
        // given
        Mockito.`when`(refreshTokenRepository.findByUser(user)).thenReturn(java.util.Optional.empty())

        // when
        spotifyLinkService.saveSpotifyToken(user, "ref-token")

        // then
        Mockito.verify(refreshTokenRepository).save(
            argThat {
                this.user == user && it.spotifyRefreshToken == "ref-token"
            }
        )
    }

    @Test
    fun `saveSpotifyToken - updates existing token if present`() {
        // given
        val existingToken = RefreshToken(
            user = user,
            refreshToken = "old-token",
            spotifyRefreshToken = "old-spotify-token",
            createdAt = LocalDateTime.now()
        )
        Mockito.`when`(refreshTokenRepository.findByUser(user)).thenReturn(java.util.Optional.of(existingToken))

        // when
        spotifyLinkService.saveSpotifyToken(user, "ref-token")

        // then
        Mockito.verify(refreshTokenRepository).save(
            argThat {
                this.user == user && it.spotifyRefreshToken == "ref-token"
            }
        )
    }
}