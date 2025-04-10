package com.team01.project.domain.music.controller

import com.team01.project.domain.music.dto.MusicResponse
import com.team01.project.domain.music.dto.SpotifyPlaylistResponse
import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.music.service.MusicService
import com.team01.project.domain.music.service.SpotifyService
import com.team01.project.global.dto.RsData
import com.team01.project.global.exception.MusicErrorCode
import com.team01.project.global.exception.MusicException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Music", description = "음악 API")
@RestController
@RequestMapping("/music")
class MusicController(
    private val musicService: MusicService,
    private val spotifyService: SpotifyService
) {

    @GetMapping("/spotify/{id}")
    @Operation(summary = "ID로 Spotify 음악 검색", description = "Spotify 에서 특정 ID의 음악을 검색하여 반환")
    fun getMusicFromSpotify(
        @PathVariable id: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<MusicResponse> {
        val token = user.getAttribute<String>("spotifyToken")
            ?: throw MusicException(MusicErrorCode.INVALID_SPOTIFY_TOKEN)

        val musicRequest = spotifyService.getTrackWithGenre(id, token)
        return RsData("200-1", "음악 조회 성공", MusicResponse.fromEntity(musicRequest.toEntity()))
    }

    @PostMapping("/spotify/{id}")
    @Operation(summary = "ID로 Spotify 음악 저장", description = "Spotify 에서 특정 ID의 음악 정보를 가져와 DB에 저장")
    fun saveMusicFromSpotify(
        @PathVariable id: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<String> {
        val token = user.getAttribute<String>("spotifyToken")
            ?: throw MusicException(MusicErrorCode.INVALID_SPOTIFY_TOKEN)

        val musicRequest = spotifyService.getTrackWithGenre(id, token)
        musicService.saveMusic(musicRequest.toEntity())
        return RsData("201-1", "음악 저장 성공", null)
    }

    @PostMapping("/save-all")
    @Operation(summary = "음악 리스트 저장", description = "장르 정보가 없을 경우 Spotify 조회하여 업데이트")
    fun saveAllMusic(
        @RequestBody musicList: List<Music>,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<String> {
        val token = user.getAttribute<String>("spotifyToken")
            ?: throw MusicException(MusicErrorCode.INVALID_SPOTIFY_TOKEN)

        val updated = musicList.map { music ->
            if (music.genre.isNullOrEmpty()) {
                val musicRequest = spotifyService.getTrackWithGenre(music.id, token)
                music.genre = musicRequest.genre
            }
            music
        }
        musicService.saveAllMusic(updated)
        return RsData("201-2", "음악 리스트 저장 완료", null)
    }

    @GetMapping("/spotify/search")
    @Operation(summary = "키워드로 Spotify 음악 검색", description = "Spotify 에서 키워드로 음악 검색")
    fun searchTracks(
        @RequestParam keyword: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<List<MusicResponse>> {
        val token = user.getAttribute<String>("spotifyToken")
            ?: throw MusicException(MusicErrorCode.INVALID_SPOTIFY_TOKEN)

        val tracks = spotifyService.searchByKeyword(keyword, token)
        val responses = tracks.map { MusicResponse.fromEntity(it.toEntity()) }
        return RsData("200-2", "키워드 검색 성공", responses)
    }

    @GetMapping("/spotify/artist/{artistId}/top-tracks")
    @Operation(summary = "아티스트 인기곡 조회", description = "Spotify 에서 특정 아티스트의 인기곡 반환")
    fun getTopTracksByArtist(
        @PathVariable artistId: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<List<MusicResponse>> {
        val token = user.getAttribute<String>("spotifyToken")
            ?: throw MusicException(MusicErrorCode.INVALID_SPOTIFY_TOKEN)

        val topTracks = spotifyService.getTopTracksByArtist(artistId, token)
        val responses = topTracks.map { MusicResponse.fromEntity(it.toEntity()) }
        return RsData("200-3", "아티스트 인기곡 조회 성공", responses)
    }

    @GetMapping("/spotify/playlist")
    @Operation(summary = "Spotify Playlist 목록 조회", description = "사용자 Spotify Playlist 요약 정보")
    fun getUserPlaylists(
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<List<SpotifyPlaylistResponse>> {
        val token = user.getAttribute<String>("spotifyToken")
            ?: throw MusicException(MusicErrorCode.INVALID_SPOTIFY_TOKEN)

        val playlists = spotifyService.getUserPlaylists(token)
        return RsData("200-9", "Playlist 목록 조회 성공", playlists)
    }

    @GetMapping("/spotify/playlist/{playlistId}")
    @Operation(summary = "Playlist 트랙 조회", description = "선택된 Playlist 트랙 목록 반환")
    fun getTracksFromPlaylist(
        @PathVariable playlistId: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<List<MusicResponse>> {
        val token = user.getAttribute<String>("spotifyToken")
            ?: throw MusicException(MusicErrorCode.INVALID_SPOTIFY_TOKEN)

        val tracks = spotifyService.getTracksFromPlaylist(playlistId, token)

        if (tracks.size > 20) {
            throw MusicException(MusicErrorCode.PLAYLIST_LIMIT_EXCEEDED)
        }

        val responses = tracks.map { MusicResponse.fromEntity(it.toEntity()) }
        return RsData("200-10", "Playlist 트랙 조회 성공", responses)
    }

    @GetMapping
    @Operation(summary = "모든 음악 조회", description = "DB에 저장된 모든 음악 반환")
    fun getAllMusic(): RsData<List<MusicResponse>> {
        val musicList = musicService.getAllMusic().map { MusicResponse.fromEntity(it) }
        return RsData("200-4", "전체 음악 조회 성공", musicList)
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID로 음악 조회", description = "DB 에서 ID로 음악 조회")
    fun getMusicById(@PathVariable id: String): RsData<MusicResponse> {
        val music = musicService.getMusicById(id)
        return RsData("200-5", "ID로 음악 조회 성공", MusicResponse.fromEntity(music))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "ID로 음악 삭제", description = "DB 에서 ID로 음악 삭제")
    fun deleteMusic(@PathVariable id: String): RsData<String> {
        musicService.deleteMusic(id)
        return RsData("204-1", "음악 삭제 완료", null)
    }

    @GetMapping("/recent/random/{userId}")
    @Operation(summary = "랜덤 최근 음악 조회", description = "사용자의 최근 음악 기록 중 무작위 선택")
    fun getRandomRecentMusic(@PathVariable userId: String): RsData<MusicResponse> {
        val music = musicService.getRandomRecentMusic(userId)
            .orElse(Music("", "", "", "", null, "", "", ""))
        return RsData("200-6", "랜덤 음악 조회 성공", MusicResponse.fromEntity(music))
    }
}
