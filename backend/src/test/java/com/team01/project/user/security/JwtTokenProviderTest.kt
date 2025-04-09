package com.team01.project.user.security

import com.team01.project.global.security.JwtTokenProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Test
    fun testCreateToken() {
        val userId = "asdf1234"
        val spotifyAccessToken = "spotify-accesstoken"

        val token = jwtTokenProvider.generateJwtToken(userId, spotifyAccessToken)
        val extractedUserId = jwtTokenProvider.getUserIdFromToken(token)

        println("토큰: $token")
        println("토큰에서 추출한 사용자 ID: $extractedUserId")
        assertNotNull(token)
        assertEquals(userId, extractedUserId)
    }

    @Test
    fun testValidateTokenValid() {
        val userId = "asdf1234"
        val spotifyAccessToken = "spotify-accesstoken"
        val token = jwtTokenProvider.generateJwtToken(userId, spotifyAccessToken)

        println("서버토큰: $token")
        val isValid = jwtTokenProvider.validateToken(token)
        assertTrue(isValid)
    }

    @Test
    fun testValidateTokenInvalid() {
        val invalidToken = "invalid.token.value"
        val isValid = jwtTokenProvider.validateToken(invalidToken)
        assertFalse(isValid)
    }
}
