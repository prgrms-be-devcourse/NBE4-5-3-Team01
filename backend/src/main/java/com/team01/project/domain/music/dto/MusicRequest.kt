package com.team01.project.domain.music.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.team01.project.domain.music.entity.Music
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class MusicRequest(

    @field:NotBlank
    val id: String,

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val singer: String,

    @field:NotBlank
    val singerId: String,

    @field:NotNull
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val releaseDate: LocalDate,

    @field:NotBlank
    val albumImage: String,

    var genre: String? = null,

    var uri: String? = null

) {
    fun toEntity(): Music {
        return Music(id, name, singer, singerId, releaseDate, albumImage, genre, uri)
    }

    fun setGenres(genres: String) {
        this.genre = genres
    }
}
