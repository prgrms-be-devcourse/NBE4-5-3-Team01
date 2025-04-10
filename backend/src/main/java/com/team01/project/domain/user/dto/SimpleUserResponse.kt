package com.team01.project.domain.user.dto

import com.team01.project.domain.user.entity.CalendarVisibility
import com.team01.project.domain.user.entity.User

/**
 * 유저 데이터 응답 DTO
 */
data class SimpleUserResponse(
    val id: String,
    val name: String,
    val profileImg: String?,
    val originalName: String?,
    val calendarVisibility: CalendarVisibility
) {
    companion object {
        fun from(user: User): SimpleUserResponse {
            return SimpleUserResponse(
                user.id,
                user.name,
                user.image,
                user.originalName,
                user.calendarVisibility
            )
        }
    }
}
