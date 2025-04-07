package com.team01.project.domain.music.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.team01.project.domain.music.entity.Music
import java.time.LocalDate

data class MusicResponse(
    val id: String,
    val name: String,
    val singer: String,
    val singerId: String,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    val releaseDate: LocalDate?,

    val albumImage: String,
    val genre: String?,
    val uri: String?
) {
    companion object {
        fun fromEntity(music: Music): MusicResponse {
            return MusicResponse(
                id = music.id,
                name = music.name,
                singer = music.singer,
                singerId = music.singerId,
                releaseDate = music.releaseDate,
                albumImage = music.albumImage,
                genre = music.genre,
                uri = music.uri
            )
        }
    }
}
