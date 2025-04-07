package com.team01.project.domain.music.dto

data class SpotifyArtistResponse(
    val id: String,
    val name: String,
    val genres: List<String>? = null
)
