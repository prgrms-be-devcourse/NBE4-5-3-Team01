package com.team01.project.domain.calendardate.controller.dto.request

/**
 * 음악 기록 수정 요청 DTO
 * @param musicIds 해당 캘린더에 기록할 전체 음악 아이디 리스트
 */

data class CalendarDateMusicSaveRequest(
    val musicIds: List<String>
)
