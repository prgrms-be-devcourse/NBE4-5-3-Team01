package com.team01.project.domain.calendardate.controller.dto.request;

import java.util.List;

/**
 * 캘린더 생성 요청 DTO
 * @param memo 메모
 * @param musicIds 음악 아이디 리스트
 */
public record CalendarDateCreateRequest(
	String memo,
	List<String> musicIds
) {

	public CalendarDateCreateRequest(
		String memo,
		List<String> musicIds
	) {
		this.memo = memo == null ? "" : memo;
		this.musicIds = musicIds == null ? List.of() : musicIds;
	}

}