package com.team01.project.domain.calendardate.controller.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.music.dto.MusicResponse;
import com.team01.project.domain.music.entity.Music;
import com.team01.project.global.permission.CalendarPermission;

/**
 * 캘린더 조회 응답 DTO
 * @param id 캘린더 아이디
 * @param date 날짜
 * @param memo 작성한 메모
 * @param musics 기록된 음악 리스트
 * @param calendarPermission 캘린더 권한
 */
public record CalendarDateFetchResponse(

	Long id,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	LocalDate date,

	String memo,

	List<MusicResponse> musics,

	CalendarPermission calendarPermission

) {

	public static CalendarDateFetchResponse of(
		CalendarDate calendarDate,
		List<Music> musics,
		CalendarPermission calendarPermission
	) {
		return new CalendarDateFetchResponse(
			calendarDate.getId(),
			calendarDate.getDate(),
			calendarDate.getMemo(),
			musics.stream().map(MusicResponse::fromEntity).toList(),
			calendarPermission
		);
	}

}