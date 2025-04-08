package com.team01.project.domain.music.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyPlaylistResponse(
    @JsonProperty("id")
    val id: String,

    @JsonProperty("name")
    val name: String,

    val image: String,
    val trackCount: Int
)
