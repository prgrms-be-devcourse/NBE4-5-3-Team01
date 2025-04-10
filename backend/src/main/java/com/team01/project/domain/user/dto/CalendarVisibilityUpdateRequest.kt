package com.team01.project.domain.user.dto

import com.team01.project.domain.user.entity.CalendarVisibility

/**
 * 캘린더 공개 여부 변경 요청 DTO
 * @param calendarVisibility 변경된 공개 여부 설정
 */
data class CalendarVisibilityUpdateRequest(
    val calendarVisibility: CalendarVisibility
)
