package com.team01.project.domain.user.dto

import com.team01.project.domain.user.entity.CalendarVisibility
import com.team01.project.domain.user.entity.User
import java.time.LocalDate
import java.time.LocalDateTime

data class UserDto(
    val id: String?,
    val password: String?,
    val email: String?,
    val name: String?,
    val originalName: String?,
    val userIntro: String?,
    val image: String?,
    val birthDay: LocalDate?,
    val createdDate: LocalDateTime?,
    val field: String?,
    val calendarVisibility: CalendarVisibility,
) {
    companion object {
        fun from(user: User): UserDto {
            return UserDto(
                id = user.id,
                password = user.userPassword,
                email = user.email,
                name = user.name,
                originalName = user.originalName,
                userIntro = user.userIntro,
                image = user.image,
                birthDay = user.birthDay,
                createdDate = user.createdDate,
                field = user.field,
                calendarVisibility = user.calendarVisibility
            )
        }
    }
}
