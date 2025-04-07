package com.team01.project.domain.calendardate.controller.dto.request

import jakarta.validation.constraints.Size

/**
 * 캘린더 생성 요청 DTO
 * @param memo 메모
 * @param musicIds 음악 아이디 리스트
 */
data class CalendarDateCreateRequest(

    @field:Size(max = 1000, message = "메모는 1000자 이내로 입력해주세요.")
    val memo: String = "",
    val musicIds: List<String> = emptyList()

)