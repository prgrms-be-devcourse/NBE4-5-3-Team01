package com.team01.project.domain.calendardate.controller.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.music.entity.Music;

/**
 * 먼슬리 캘린더 조회 응답 DTO
 * @param monthly 먼슬리 캘린더
 */
public record MonthlyFetchResponse(
	List<SingleCalendarDate> monthly
) {

	/**
	 * 캘린더 조회 응답 DTO
	 * @param id 캘린더 아이디
	 * @param date 날짜
	 * @param hasMemo 메모 작성 여부
	 * @param albumImage 해당 캘린더에 기록된 음악의 앨범 이미지 하나
	 */
	public record SingleCalendarDate(

		Long id,

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		@JsonDeserialize(using = LocalDateDeserializer.class)
		LocalDate date,

		boolean hasMemo,

		String albumImage

	) {

		public static SingleCalendarDate of(CalendarDate calendarDate, Music music) {
			return new SingleCalendarDate(
				calendarDate.getId(),
				calendarDate.getDate(),
				!(calendarDate.getMemo().isBlank()),
				music != null ? music.getAlbumImage() : "");
		}

		public static SingleCalendarDate from(CalendarDate calendarDate) {
			return of(calendarDate, null);
		}

	}

}