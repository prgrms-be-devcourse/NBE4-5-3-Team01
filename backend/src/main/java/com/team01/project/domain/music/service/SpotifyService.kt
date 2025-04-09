package com.team01.project.domain.music.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.team01.project.domain.music.dto.MusicRequest
import com.team01.project.domain.music.dto.SpotifyArtistResponse
import com.team01.project.domain.music.dto.SpotifyPlaylistResponse
import com.team01.project.domain.music.dto.SpotifyTrackResponse
import com.team01.project.global.exception.SpotifyApiException
import com.team01.project.global.exception.SpotifyErrorCode
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class SpotifyService {

    private val webClient: WebClient = WebClient.builder()
        .baseUrl("https://api.spotify.com/v1")
        .filter(errorHandlingFilter())
        .exchangeStrategies(
            ExchangeStrategies.builder()
                .codecs { config -> config.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) }
                .build()
        )
        .build()

    private val objectMapper = ObjectMapper()
    private val genreCache: MutableMap<String, List<String>> = mutableMapOf()

    private fun extractToken(accessToken: String): String =
        if (accessToken.startsWith("Bearer ")) accessToken.substring(7) else accessToken

    private fun errorHandlingFilter(): ExchangeFilterFunction =
        ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            when {
                clientResponse.statusCode().is5xxServerError -> Mono.error(SpotifyApiException(SpotifyErrorCode.SERVER_ERROR))
                clientResponse.statusCode().is4xxClientError -> Mono.error(SpotifyApiException(SpotifyErrorCode.INVALID_REQUEST))
                else -> Mono.just(clientResponse)
            }
        }

    fun getTrackInfo(trackId: String, accessToken: String): SpotifyTrackResponse {
        val token = extractToken(accessToken)
        return try {
            webClient.get()
                .uri("/tracks/$trackId?market=KR")
                .headers { it.setBearerAuth(token) }
                .retrieve()
                .bodyToMono(SpotifyTrackResponse::class.java)
                .block() ?: throw SpotifyApiException(SpotifyErrorCode.TRACK_NOT_FOUND)
        } catch (e: Exception) {
            throw SpotifyApiException(SpotifyErrorCode.UNKNOWN, e)
        }
    }

    fun getArtistGenres(artistId: String, accessToken: String): List<String> {
        genreCache[artistId]?.let { return it }

        val token = extractToken(accessToken)
        return try {
            val response = webClient.get()
                .uri("/artists/$artistId")
                .headers { it.setBearerAuth(token) }
                .retrieve()
                .bodyToMono(SpotifyArtistResponse::class.java)
                .block()

            val genres = response?.genres ?: emptyList()
            genreCache[artistId] = genres
            genres
        } catch (e: Exception) {
            throw SpotifyApiException(SpotifyErrorCode.ARTIST_GENRE_NOT_FOUND, e)
        }
    }

    fun getTrackWithGenre(trackId: String, accessToken: String): MusicRequest {
        val track = getTrackInfo(trackId, accessToken)

        val artistIds = track.artists.map { it.id }
        val allGenres = artistIds.flatMap { getArtistGenres(it, accessToken) }.toSet()
        val parsedReleaseDate = parseReleaseDate(track.album.releaseDate)

        return MusicRequest(
            id = track.id,
            name = track.name,
            singer = track.getArtistsAsString(),
            singerId = track.getArtistsIdAsString(),
            releaseDate = parsedReleaseDate,
            albumImage = track.album.images.firstOrNull()?.url ?: "",
            genre = allGenres.joinToString(", "),
            uri = track.uri
        )
    }

    fun searchByKeyword(keyword: String, accessToken: String): List<MusicRequest> {
        val token = extractToken(accessToken)
        val url = "/search?q=$keyword&type=track&limit=10&market=KR"

        return try {
            val jsonResponse = webClient.get()
                .uri(url)
                .headers { it.setBearerAuth(token) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block() ?: throw SpotifyApiException(SpotifyErrorCode.TRACK_NOT_FOUND)

            val root = objectMapper.readTree(jsonResponse)
            val items = root.path("tracks").path("items")
            if (!items.isArray) throw SpotifyApiException(SpotifyErrorCode.TRACK_NOT_FOUND)

            items.map { item ->
                val track = objectMapper.treeToValue(item, SpotifyTrackResponse::class.java)
                track.toMusicRequest(parseReleaseDate(track.album.releaseDate))
            }
        } catch (e: WebClientResponseException) {
            throw SpotifyApiException(SpotifyErrorCode.INVALID_REQUEST, e)
        } catch (e: Exception) {
            throw SpotifyApiException(SpotifyErrorCode.UNKNOWN, e)
        }
    }

    fun getTopTracksByArtist(artistId: String, accessToken: String): List<MusicRequest> {
        val token = extractToken(accessToken)

        return try {
            val jsonResponse = webClient.get()
                .uri("/artists/$artistId/top-tracks?market=KR")
                .headers { it.setBearerAuth(token) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block() ?: throw SpotifyApiException(SpotifyErrorCode.TRACK_NOT_FOUND)

            val tracks = objectMapper.readTree(jsonResponse).path("tracks")
            if (!tracks.isArray) throw SpotifyApiException(SpotifyErrorCode.TRACK_NOT_FOUND)

            tracks.map { node ->
                val track = objectMapper.treeToValue(node, SpotifyTrackResponse::class.java)
                track.toMusicRequest(parseReleaseDate(track.album.releaseDate))
            }
        } catch (e: WebClientResponseException) {
            throw SpotifyApiException(SpotifyErrorCode.INVALID_REQUEST, e)
        } catch (e: Exception) {
            throw SpotifyApiException(SpotifyErrorCode.UNKNOWN, e)
        }
    }

    fun getUserPlaylists(accessToken: String): List<SpotifyPlaylistResponse> {
        val token = extractToken(accessToken)

        return try {
            val jsonResponse = webClient.get()
                .uri("/me/playlists?limit=10")
                .headers { it.setBearerAuth(token) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block() ?: throw SpotifyApiException(SpotifyErrorCode.PLAYLIST_NOT_FOUND)

            val items = objectMapper.readTree(jsonResponse).path("items")

            items.map { item ->
                val id = item.path("id").asText()
                val name = item.path("name").asText()
                val image = item.path("images").firstOrNull()?.path("url")?.asText() ?: ""
                val trackCount = item.path("tracks").path("total").asInt()
                SpotifyPlaylistResponse(id, name, image, trackCount)
            }
        } catch (e: Exception) {
            throw SpotifyApiException(SpotifyErrorCode.UNKNOWN, e)
        }
    }

    fun getTracksFromPlaylist(playlistId: String, accessToken: String): List<MusicRequest> {
        val token = extractToken(accessToken)

        return try {
            val jsonResponse = webClient.get()
                .uri("/playlists/$playlistId/tracks?market=KR")
                .headers { it.setBearerAuth(token) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block() ?: throw SpotifyApiException(SpotifyErrorCode.PLAYLIST_NOT_FOUND)

            val items = objectMapper.readTree(jsonResponse).path("items")

            items.mapNotNull { item ->
                val trackNode = item.path("track")
                if (trackNode.isMissingNode || trackNode.isNull) return@mapNotNull null

                val track = objectMapper.treeToValue(trackNode, SpotifyTrackResponse::class.java)
                track.toMusicRequest(parseReleaseDate(track.album.releaseDate))
            }
        } catch (e: Exception) {
            throw SpotifyApiException(SpotifyErrorCode.UNKNOWN, e)
        }
    }

    private fun parseReleaseDate(releaseDate: String?): LocalDate? {
        if (releaseDate.isNullOrBlank()) null

        return try {
            when (releaseDate?.length) {
                4 -> LocalDate.of(releaseDate.toInt(), 1, 1)
                7 -> LocalDate.parse("$releaseDate-01", DateTimeFormatter.ISO_DATE)
                else -> LocalDate.parse(releaseDate, DateTimeFormatter.ISO_DATE)
            }
        } catch (e: Exception) {
            throw SpotifyApiException(SpotifyErrorCode.UNKNOWN, IllegalArgumentException("날짜 형식 오류: $releaseDate", e))
        }
    }

    private fun SpotifyTrackResponse.toMusicRequest(parsedDate: LocalDate?): MusicRequest {
        return MusicRequest(
            id = this.id,
            name = this.name,
            singer = this.getArtistsAsString(),
            singerId = this.getArtistsIdAsString(),
            releaseDate = parsedDate,
            albumImage = this.album.images.firstOrNull()?.url ?: "",
            genre = null,
            uri = this.uri
        )
    }
}
