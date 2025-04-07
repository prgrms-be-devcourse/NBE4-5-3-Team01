package com.team01.project.domain.user.repository

import com.team01.project.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {

    override fun findAll(): List<User>

    @Query("SELECT u FROM User u WHERE u.name LIKE %:q% OR u.originalName LIKE %:q%")
    fun searchUser(@Param("q") name: String): List<User>

    override fun getById(userId: String): User {
        return findById(userId)
            .orElseThrow { IllegalArgumentException("유저를 찾을 수 없습니다.") }
    }

    override fun existsById(id: String): Boolean
}
