package com.team01.project.domain.music.dto

data class SpotifyPlaylistResponse(
    val id: String,
    val name: String,
    val image: String,
    val trackCount: Int
)
