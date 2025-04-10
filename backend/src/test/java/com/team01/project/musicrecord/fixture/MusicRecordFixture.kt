package com.team01.project.musicrecord.fixture

import com.team01.project.calendardate.fixture.CalendarDateFixture
import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.musicrecord.entity.MusicRecord
import com.team01.project.domain.musicrecord.entity.MusicRecordId
import com.team01.project.music.fixture.MusicFixture

object MusicRecordFixture {
    fun musicRecord(
        calendarDateId: Long = 1L,
        musicId: String = "music id",
        calendarDate: CalendarDate = CalendarDateFixture.calendarDate(id = calendarDateId),
        music: Music = MusicFixture.music(id = musicId)
    ): MusicRecord {
        val musicRecordId = MusicRecordId(calendarDateId = calendarDateId, musicId = musicId)

        return MusicRecord(
            id = musicRecordId,
            calendarDate = calendarDate,
            music = music
        )
    }
}
