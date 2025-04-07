package com.team01.project.domain.calendardate.controller.dto.request

/**
 * 캘린더 생성 요청 DTO
 * @param memo 메모
 * @param musicIds 음악 아이디 리스트
 */
data class CalendarDateCreateRequest(
    val memo: String = "",
    val musicIds: List<String> = emptyList()
)