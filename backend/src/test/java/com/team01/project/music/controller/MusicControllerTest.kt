package com.team01.project.music.controller

import com.team01.project.domain.music.controller.MusicController
import com.team01.project.domain.music.dto.MusicRequest
import com.team01.project.domain.music.dto.SpotifyPlaylistResponse
import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.music.service.MusicService
import com.team01.project.domain.music.service.SpotifyService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate
import java.util.*

class MusicControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var musicService: MusicService
    private lateinit var spotifyService: SpotifyService
    private val token = "test-token"

    @BeforeEach
    fun setUp() {
        musicService = mockk()
        spotifyService = mockk()
        val controller = MusicController(musicService, spotifyService)

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(AuthenticationPrincipalArgumentResolver())
            .build()

        val oauthUser = mockk<OAuth2User>()
        every { oauthUser.name } returns "user123"
        every { oauthUser.getAttribute<String>("spotifyToken") } returns "test-token" // ✅ 추가

        val auth = UsernamePasswordAuthenticationToken(
            oauthUser,
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )

        SecurityContextHolder.getContext().authentication = auth
    }

    @Test
    @DisplayName("Spotify 음악 조회")
    fun getMusicFromSpotify() {
        val music = Music("id1", "title", "singer", "singerId", LocalDate.now(), "image", "genre", "uri")
        val musicRequest = music.toRequest()
        every { spotifyService.getTrackWithGenre("id1", token) } returns musicRequest

        mockMvc.get("/music/spotify/id1")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.id") { value("id1") }
            }
    }

    @Test
    @DisplayName("Spotify 음악 저장")
    fun saveMusicFromSpotify() {
        val music = Music("id2", "title2", "singer2", "singerId2", LocalDate.now(), "image2", "genre2", "uri2")
        val musicRequest = music.toRequest()

        every { spotifyService.getTrackWithGenre("id2", "test-token") } returns musicRequest
        every { spotifyService.searchByKeyword("chill", "test-token") } returns listOf(musicRequest) // ✅ 이거 추가
        every { musicService.saveMusic(any()) } returns music

        mockMvc.get("/music/spotify/search?keyword=chill")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    @DisplayName("음악 리스트 저장")
    fun saveAllMusic() {
        val music = Music("id3", "title3", "singer3", "singerId3", null, "image3", null, null)
        val updatedRequest = MusicRequest(
            id = music.id,
            name = music.name,
            singer = music.singer,
            singerId = music.singerId,
            releaseDate = music.releaseDate,
            albumImage = music.albumImage,
            genre = "pop", // 직접 수정
            uri = music.uri
        )

        every { spotifyService.getTrackWithGenre(music.id, token) } returns updatedRequest
        every { musicService.saveAllMusic(any()) } returns listOf(music)

        mockMvc.post("/music/save-all") {
            contentType = MediaType.APPLICATION_JSON
            content = """
        [
            {
                "id": "id3",
                "name": "title3",
                "singer": "singer3",
                "singerId": "singerId3",
                "releaseDate": null,
                "albumImage": "image3",
                "genre": null,
                "uri": null
            }
        ]
    """.trimIndent()
        }
    }

    @Test
    @DisplayName("키워드로 Spotify 음악 검색")
    fun searchTracks() {
        val music = Music("id8", "title8", "singer8", "singerId8", LocalDate.now(), "image8", "genre8", "uri8")
        val musicRequest = music.toRequest()
        every { spotifyService.searchByKeyword("chill", token) } returns listOf(musicRequest)

        mockMvc.get("/music/spotify/search?keyword=chill")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.size()") { value(1) }
                jsonPath("$.data[0].id") { value("id8") }
            }
    }

    @Test
    @DisplayName("아티스트 인기곡 조회")
    fun getTopTracksByArtist() {
        val music = Music("id9", "title9", "singer9", "singerId9", LocalDate.now(), "image9", "genre9", "uri9")
        val musicRequest = music.toRequest()
        every { spotifyService.getTopTracksByArtist("artist123", token) } returns listOf(musicRequest)

        mockMvc.get("/music/spotify/artist/artist123/top-tracks")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.size()") { value(1) }
                jsonPath("$.data[0].id") { value("id9") }
            }
    }

    @Test
    @DisplayName("Spotify 플레이리스트 목록 조회")
    fun getUserPlaylists() {
        val playlist = SpotifyPlaylistResponse("playlist1", "Test Playlist", "image-url", 10)
        every { spotifyService.getUserPlaylists(token) } returns listOf(playlist)

        mockMvc.get("/music/spotify/playlist")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.size()") { value(1) }
                jsonPath("$.data[0].id") { value("playlist1") }
            }
    }

    @Test
    @DisplayName("플레이리스트 트랙 조회")
    fun getTracksFromPlaylist() {
        val music = Music("id10", "title10", "singer10", "singerId10", LocalDate.now(), "image10", "genre10", "uri10")
        val musicRequest = music.toRequest()
        every { spotifyService.getTracksFromPlaylist("playlist1", token) } returns listOf(musicRequest)

        mockMvc.get("/music/spotify/playlist/playlist1")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.size()") { value(1) }
                jsonPath("$.data[0].id") { value("id10") }
            }
    }

    @Test
    @DisplayName("전체 음악 조회")
    fun getAllMusic() {
        val music = Music("id4", "title4", "singer4", "singerId4", LocalDate.now(), "image4", "genre4", "uri4")
        every { musicService.getAllMusic() } returns listOf(music)

        mockMvc.get("/music")
            .andExpect {
                status { isOk() }
                jsonPath("$.data[0].id") { value("id4") }
            }
    }

    @Test
    @DisplayName("ID로 음악 조회")
    fun getMusicById() {
        val music = Music("id5", "title5", "singer5", "singerId5", LocalDate.now(), "image5", "genre5", "uri5")
        every { musicService.getMusicById("id5") } returns music

        mockMvc.get("/music/id5")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.id") { value("id5") }
            }
    }

    @Test
    @DisplayName("ID로 음악 삭제")
    fun deleteMusic() {
        every { musicService.deleteMusic("id6") } returns Unit

        mockMvc.delete("/music/id6")
            .andExpect {
                status { isOk() } // ✅ RsData를 쓰면 항상 isOk()
                jsonPath("$.code") { value("204-1") }
                jsonPath("$.msg") { value("음악 삭제 완료") }
            }
    }

    @Test
    @DisplayName("랜덤 최근 음악 조회")
    fun getRandomRecentMusic() {
        val music = Music("id7", "title7", "singer7", "singerId7", LocalDate.now(), "image7", "genre7", "uri7")
        every { musicService.getRandomRecentMusic("user1") } returns Optional.of(music)

        mockMvc.get("/music/recent/random/user1")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.id") { value("id7") }
            }
    }

    // 확장 함수로 변환 코드 분리
    private fun Music.toRequest(): MusicRequest {
        return MusicRequest(
            id = id,
            name = name,
            singer = singer,
            singerId = singerId,
            releaseDate = releaseDate,
            albumImage = albumImage,
            genre = genre,
            uri = uri
        )
    }
}
