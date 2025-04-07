package com.team01.project.domain.music.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyTrackResponse(
    val id: String,
    val name: String,

    @JsonProperty("uri")
    val uri: String,

    @JsonProperty("artists")
    val artists: List<Artist>,

    val album: Album
) {
    fun getArtistsAsString(): String {
        return artists.joinToString(", ") { it.name }
    }

    fun getArtistsIdAsString(): String {
        return artists.joinToString(", ") { it.id }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Album(
        val name: String,

        @JsonProperty("release_date")
        val releaseDate: String,

        @JsonProperty("images")
        val images: List<Image>
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Artist(
        val id: String,
        val name: String
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Image(
        val url: String
    )
}
