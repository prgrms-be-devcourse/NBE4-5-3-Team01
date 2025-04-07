package com.team01.project.domain.musicrecord.controller.dto

import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.musicrecord.entity.MusicRecord
import java.time.LocalDate

data class MusicRecordDto(

    // CalendarDate 관련 정보
    val calendarDateId: Long,
    val date: LocalDate,
    val memo: String,

    // Music 관련 정보
    val musicId: String,
    val musicName: String,
    val singer: String,
    val singerId: String,
    val releaseDate: LocalDate?,
    val albumImage: String,
    val genre: String?

) {
    companion object {
        // 엔티티 -> DTO 변환 메서드
		fun from(musicRecord: MusicRecord): MusicRecordDto {
            val calendarDate: CalendarDate = musicRecord.calendarDate
            val music: Music = musicRecord.music

            return MusicRecordDto(
                calendarDateId = calendarDate.id!!,
                date = calendarDate.date,
                memo = calendarDate.memo,
                musicId = music.id,
                musicName = music.name,
                singer = music.singer,
                singerId = music.singerId,
                releaseDate = music.releaseDate,
                albumImage = music.albumImage,
                genre = music.genre
            )
        }
    }
}
