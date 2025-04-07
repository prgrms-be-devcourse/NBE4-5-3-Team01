package com.team01.project.domain.user.repository

import com.team01.project.domain.user.entity.RefreshToken
import com.team01.project.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByUserId(userId: String): Optional<RefreshToken>
    fun findByUser(user: User): Optional<RefreshToken>
    fun findTopByUserIdOrderByCreatedAtDesc(userId: String): Optional<RefreshToken>

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.user.id = :userId")
    fun deleteByUserId(userId: String)

    fun user(user: User): String
}
