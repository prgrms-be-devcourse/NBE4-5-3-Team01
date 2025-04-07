package com.team01.project.domain.calendardate.controller.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.music.entity.Music
import java.time.LocalDate

/**
 * 먼슬리 캘린더 조회 응답 DTO
 * @param monthly 먼슬리 캘린더
 */
data class MonthlyFetchResponse(
    val monthly: List<SingleCalendarDate>
) {
    /**
     * 캘린더 조회 응답 DTO
     * @param id 캘린더 아이디
     * @param date 날짜
     * @param hasMemo 메모 작성 여부
     * @param albumImage 해당 캘린더에 기록된 음악의 앨범 이미지 하나
     */
    data class SingleCalendarDate(
        val id: Long,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @JsonDeserialize(using = LocalDateDeserializer::class)
        val date: LocalDate,

        val hasMemo: Boolean,

        val albumImage: String
    ) {
        companion object {
            fun of(calendarDate: CalendarDate, music: Music?): SingleCalendarDate {
                return SingleCalendarDate(
                    id = calendarDate.id!!,
                    date = calendarDate.date,
                    hasMemo = calendarDate.memo.isNotBlank(),
                    albumImage = music?.albumImage ?: ""
                )
            }

            fun from(calendarDate: CalendarDate): SingleCalendarDate {
                return of(calendarDate, null)
            }
        }
    }
}
