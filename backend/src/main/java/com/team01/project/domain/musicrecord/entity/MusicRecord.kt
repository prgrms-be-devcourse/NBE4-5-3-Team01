package com.team01.project.domain.musicrecord.entity

import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.music.entity.Music
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId

@Entity
class MusicRecord(

    @EmbeddedId
    val id: MusicRecordId,

    @MapsId("calendarDateId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_date_id", nullable = false)
    val calendarDate: CalendarDate,

    @MapsId("musicId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", nullable = false)
    val music: Music

)
