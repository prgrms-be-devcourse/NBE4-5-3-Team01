package com.team01.project.user.repository

import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun testCreateUser() {
        val localDate: LocalDate = LocalDate.parse("2000-01-02", DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val user = User(
            id = "asdf1234",
            email = "test@example.com",
            createdDate = LocalDateTime.now(),
            name = "name",
            originalName = "nickName",
            birthDay = localDate,
            field = "사용자"
            // 나머지 선택적 필드는 기본값 사용
        )

        userRepository.save(user)
    }
}
