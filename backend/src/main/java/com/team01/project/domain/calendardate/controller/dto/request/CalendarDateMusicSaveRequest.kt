package com.team01.project.domain.calendardate.controller.dto.request;

import java.util.List;

/**
 * 음악 기록 저장 요청 DTO
 * @param musicIds 해당 캘린더에 기록할 전체 음악 아이디 리스트
 */
public record CalendarDateMusicSaveRequest(
	List<String> musicIds
) {
}