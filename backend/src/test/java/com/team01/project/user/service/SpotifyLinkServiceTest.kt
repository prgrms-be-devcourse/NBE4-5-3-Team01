package com.team01.project.domain.user.service

import com.team01.project.domain.user.entity.RefreshToken
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.RefreshTokenRepository
import com.team01.project.domain.user.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.argThat
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class SpotifyLinkServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @InjectMocks
    private lateinit var spotifyLinkService: SpotifyLinkService

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = User(
            id = "test-user",
            email = "test@example.com",
            name = "tester",
            createdDate = LocalDateTime.now()
        )
    }

    @Test
    fun `saveSpotifyToken - saves new token when token not exists`() {
        // given
        `when`(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty())

        // when
        spotifyLinkService.saveSpotifyToken(user, "ref-token")

        // then
        verify(refreshTokenRepository).save(
            argThat {
                it.user == user && it.spotifyRefreshToken == "ref-token"
            }
        )
    }

    @Test
    fun `saveSpotifyToken - updates existing token if present`() {
        // given
        val existingToken =
            RefreshToken(user = user, spotifyRefreshToken = "old-token", createdAt = LocalDateTime.now())
        `when`(refreshTokenRepository.findByUser(user)).thenReturn(Optional.of(existingToken))

        // when
        spotifyLinkService.saveSpotifyToken(user, "new-ref-token")

        // then
        verify(refreshTokenRepository).save(
            argThat {
                it.user == user && it.spotifyRefreshToken == "ref-token"
            }
        )
    }
}
