package com.team01.project.domain.musicrecord.entity

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class MusicRecordId(
    val calendarDateId: Long,
    val musicId: String
) : Serializable
