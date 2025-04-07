package com.team01.project.domain.follow.repository

import com.team01.project.domain.follow.entity.Follow
import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface FollowRepository : JpaRepository<Follow?, Long?> {
    fun findByToUserAndFromUser(toUser: User, fromUser: User): Optional<Follow>

    fun findByFromUserAndStatus(fromUser: User, status: Status): List<Follow>

    fun findByFromUser(fromUser: User): List<Follow>

    fun findByToUserAndStatus(toUser: User, status: Status?): List<Follow>

    fun existsByToUserAndFromUser(toUser: User, fromUser: User): Boolean

    fun countByFromUserAndStatus(fromUser: User, status: Status): Long

    fun countByToUserAndStatus(toUser: User, status: Status): Long

    @Query("SELECT f.status FROM Follow f WHERE f.toUser = :toUser AND f.fromUser = :fromUser")
    fun findStatusByToUserAndFromUser(
        @Param("toUser") toUser: User,
        @Param("fromUser") fromUser: User
    ): Optional<Status>
}
