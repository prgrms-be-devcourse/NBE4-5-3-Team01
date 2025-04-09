package com.team01.project.user.repository

import com.team01.project.domain.user.entity.RefreshToken
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.RefreshTokenRepository
import com.team01.project.domain.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class RefreshTokenRepositoryTest {

    @Autowired
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun testCreateRefreshToken() {
        val userOptional = userRepository.findById("1234")
        if (userOptional.isEmpty) {
            println("유저 아이디 중 1234가 없음.")
            return
        }

        val user: User = userOptional.get()

        val refreshToken = RefreshToken(
            user = user,
            refreshToken = "sampleRefreshToken",
            createdAt = LocalDateTime.now()
        )

        val saveToken = refreshTokenRepository.save(refreshToken)
        println("리프레시 토큰 저장 완료: ${saveToken.refreshToken}")
    }
}
