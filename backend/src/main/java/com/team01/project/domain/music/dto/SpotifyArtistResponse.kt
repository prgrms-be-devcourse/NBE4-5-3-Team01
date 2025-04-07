package com.team01.project.domain.music.dto

data class SpotifyArtistResponse(
    var id: String,
    var name: String,
    var genres: List<String>
)
