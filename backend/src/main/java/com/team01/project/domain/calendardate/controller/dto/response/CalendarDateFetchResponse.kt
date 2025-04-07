package com.team01.project.domain.calendardate.controller.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.music.dto.MusicResponse
import com.team01.project.domain.music.entity.Music
import com.team01.project.global.permission.CalendarPermission
import java.time.LocalDate

/**
 * 캘린더 조회 응답 DTO
 * @param id 캘린더 아이디
 * @param date 날짜
 * @param memo 작성한 메모
 * @param musics 기록된 음악 리스트
 * @param calendarPermission 조회 요청한 유저가 이 캘린더에 대해 갖는 권한
 */
data class CalendarDateFetchResponse(

    val id: Long,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    val date: LocalDate,

    val memo: String,

    val musics: List<MusicResponse>,

    val calendarPermission: CalendarPermission

) {
    companion object {
        fun of(
            calendarDate: CalendarDate,
            musics: List<Music>,
            calendarPermission: CalendarPermission
        ): CalendarDateFetchResponse {
            return CalendarDateFetchResponse(
                id = calendarDate.id!!,
                date = calendarDate.date,
                memo = calendarDate.memo,
                musics = musics.map { MusicResponse.fromEntity(it) },
                calendarPermission = calendarPermission
            )
        }
    }
}
