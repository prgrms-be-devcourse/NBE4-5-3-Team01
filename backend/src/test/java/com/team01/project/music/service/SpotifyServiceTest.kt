package com.team01.project.domain.music.service

import com.fasterxml.jackson.databind.JsonNode
import com.team01.project.global.exception.SpotifyApiException
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.web.reactive.function.client.WebClient
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpotifyServiceTest {

    private lateinit var spotifyService: SpotifyService
    private lateinit var accessToken: String

    private val clientId = "fadbee879e0e4575a2bb28abfe276934"
    private val clientSecret = "9a938eb715544e279e03a547c0e64aa5"

    private val trackId = "30HIJzJEUYcL9Qng15UeBo"
    private val artistId = "6YVMFz59CuY7ngCxTxjpxE"
    private val playlistId = "37i9dQZF1DXcBWIGoYBM5M"

    @BeforeAll
    fun setUp() {
        spotifyService = SpotifyService()
        accessToken = fetchSpotifyToken()
    }

    private fun fetchSpotifyToken(): String {
        val authHeader = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

        val response: JsonNode? = WebClient.create("https://accounts.spotify.com/api/token")
            .post()
            .header("Authorization", "Basic $authHeader")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .bodyValue("grant_type=client_credentials")
            .retrieve()
            .bodyToMono(JsonNode::class.java)
            .block()

        return "Bearer ${response?.get("access_token")?.asText() ?: throw IllegalStateException("토큰 발급 실패")}"
    }

    @Test
    @DisplayName("ID를 통해 트랙 정보 가져옴")
    fun getTrackInfoTest() {
        val result = spotifyService.getTrackInfo(trackId, accessToken)
        assertNotNull(result)
        assertEquals(trackId, result.id)
    }

    @Test
    @DisplayName("잘못된 트랙 ID 요청 시 예외 발생")
    fun getTrackInfo_ShouldThrowException() {
        assertFailsWith<SpotifyApiException> {
            spotifyService.getTrackInfo("invalid_id", accessToken)
        }
    }

    @Test
    @DisplayName("아티스트 장르 정보를 정상적으로 가져옴")
    fun getArtistGenresTest() {
        val genres = spotifyService.getArtistGenres(artistId, accessToken)
        assertNotNull(genres)
        assertTrue(genres.isNotEmpty())
    }

    @Test
    @DisplayName("트랙 + 장르 정보를 통합해서 가져옴")
    fun getTrackWithGenreTest() {
        val result = spotifyService.getTrackWithGenre(trackId, accessToken)
        assertNotNull(result)
        assertEquals(trackId, result.id)
    }

    @Test
    @DisplayName("키워드로 검색된 트랙 목록을 반환")
    fun searchByKeywordTest() {
        val results = spotifyService.searchByKeyword("lofi", accessToken)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
    }

    @Test
    @DisplayName("아티스트의 인기 트랙 목록을 반환")
    fun getTopTracksByArtistTest() {
        val results = spotifyService.getTopTracksByArtist(artistId, accessToken)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
    }

    // OAuth 인증이 필요한 API이므로 자동 테스트에서 제외됨
    // scope : playlist-read-private 주입 필요
//    @Test
//    @DisplayName("사용자 플레이리스트 목록을 가져옴")
//    fun getUserPlaylistsTest() {
//        val results = spotifyService.getUserPlaylists(accessToken)
//        assertNotNull(results)
//        assertTrue(results.isNotEmpty())
//    }
//
//    @Test
//    @DisplayName("플레이리스트의 트랙 목록을 가져옴")
//    fun getTracksFromPlaylistTest() {
//        val results = spotifyService.getTracksFromPlaylist(playlistId, accessToken)
//        assertNotNull(results)
//        assertTrue(results.isNotEmpty())
//    }
}
