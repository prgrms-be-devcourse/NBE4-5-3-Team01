package com.team01.project.domain.music.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.music.dto.MusicRequest;
import com.team01.project.domain.music.dto.MusicResponse;
import com.team01.project.domain.music.dto.SpotifyPlaylistResponse;
import com.team01.project.domain.music.entity.Music;
import com.team01.project.domain.music.service.MusicService;
import com.team01.project.domain.music.service.SpotifyService;
import com.team01.project.global.dto.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Music", description = "음악 API")
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

	private final MusicService musicService;
	private final SpotifyService spotifyService;

	@GetMapping("/spotify/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "ID로 Spotify 음악 검색",
		description = "Spotify에서 특정 ID의 음악을 검색하여 반환"
	)
	public RsData<MusicResponse> getMusicFromSpotify(
		@PathVariable String id,
		@AuthenticationPrincipal OAuth2User user
	) {
		String spotifyToken = user.getAttribute("spotifyToken");
		MusicRequest musicRequest = spotifyService.getTrackWithGenre(id, spotifyToken);
		Music music = musicRequest.toEntity();
		return new RsData<>(
			"200-1",
			"음악 조회 성공",
			MusicResponse.fromEntity(music)
		);
	}

	@PostMapping("/spotify/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(
		summary = "ID로 Spotify 음악 저장",
		description = "Spotify에서 특정 ID의 음악 정보를 가져와 DB에 저장"
	)
	public RsData<String> saveMusicFromSpotify(
		@PathVariable String id,
		@AuthenticationPrincipal OAuth2User user
	) {
		String spotifyToken = user.getAttribute("spotifyToken");
		MusicRequest musicRequest = spotifyService.getTrackWithGenre(id, spotifyToken);
		Music savedMusic = musicService.saveMusic(musicRequest.toEntity());
		return new RsData<>(
			"201-1",
			"음악 저장 성공",
			null
		);
	}

	@PostMapping("/save-all")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(
		summary = "음악 리스트 저장",
		description = "요청받은 음악 리스트를 저장하며, 장르 정보가 없을 경우 Spotify에 조회하여 업데이트"
	)
	public RsData<String> saveAllMusic(
		@RequestBody List<Music> musicList,
		@AuthenticationPrincipal OAuth2User user
	) {
		String spotifyToken = user.getAttribute("spotifyToken");
		List<Music> updatedMusicList = musicList.stream()
			.map(music -> {
				if (music.getGenre() == null || music.getGenre().isEmpty()) {
					MusicRequest musicRequest = spotifyService.getTrackWithGenre(music.getId(), spotifyToken);
					music.setGenre(musicRequest.getGenre());
				}
				return music;
			})
			.toList();
		musicService.saveAllMusic(updatedMusicList);
		return new RsData<>(
			"201-2",
			"음악 리스트 저장 완료",
			null
		);
	}

	@GetMapping("/spotify/search")
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "키워드로 Spotify에서 음악 검색",
		description = "Spotify에서 특정 키워드로 검색하여 해당되는 음악 리스트 반환"
	)
	public RsData<List<MusicResponse>> searchTracks(
		@RequestParam String keyword,
		@AuthenticationPrincipal OAuth2User user
	) {
		String spotifyToken = user.getAttribute("spotifyToken");
		List<MusicRequest> tracks = spotifyService.searchByKeyword(keyword, spotifyToken);
		List<MusicResponse> responseList = tracks.stream()
			.map(request -> MusicResponse.fromEntity(request.toEntity()))
			.toList();
		return new RsData<>(
			"200-2",
			"키워드 검색 성공",
			responseList
		);
	}

	@GetMapping("/spotify/artist/{artistId}/top-tracks")
	@Operation(
		summary = "특정 아티스트의 인기곡 조회",
		description = "Spotify에서 특정 아티스트의 인기곡 리스트 반환"
	)
	public RsData<List<MusicResponse>> getTopTracksByArtist(
		@PathVariable String artistId,
		@AuthenticationPrincipal OAuth2User user
	) {
		String spotifyToken = user.getAttribute("spotifyToken");
		List<MusicRequest> topTracks = spotifyService.getTopTracksByArtist(artistId, spotifyToken);
		List<MusicResponse> responseList = topTracks.stream()
			.map(request -> MusicResponse.fromEntity(request.toEntity()))
			.toList();
		return new RsData<>(
			"200-3",
			"아티스트 인기곡 조회 성공",
			responseList
		);
	}

	@GetMapping("/spotify/playlist")
	@Operation(
		summary = "사용자의 Spotify Playlist 목록 조회",
		description = "이름, 이미지, 트랙 수 등 포함된 요약 정보 반환"
	)
	public RsData<List<SpotifyPlaylistResponse>> getUserPlaylists(
		@AuthenticationPrincipal OAuth2User user
	) {
		String token = user.getAttribute("spotifyToken");
		List<SpotifyPlaylistResponse> playlists = spotifyService.getUserPlaylists(token);
		return new RsData<>(
			"200-9",
			"Playlist 목록 조회 성공",
			playlists
		);

	}

	@GetMapping("/spotify/playlist/{playlistId}")
	@Operation(
		summary = "특정 Spotify Playlist 트랙 조회",
		description = "선택된 Playlist의 트랙 목록 반환"
	)
	public RsData<List<MusicResponse>> getTracksFromPlaylist(
		@PathVariable String playlistId,
		@AuthenticationPrincipal OAuth2User user
	) {
		String token = user.getAttribute("spotifyToken");
		List<MusicRequest> tracks = spotifyService.getTracksFromPlaylist(playlistId, token);

		if (tracks.size() > 20) {
			return new RsData<>("400-LIMIT", "20곡 이하의 플레이리스트만 추가할 수 있습니다.", null);
		}

		List<MusicResponse> response = tracks.stream()
			.map(req -> MusicResponse.fromEntity(req.toEntity()))
			.toList();
		return new RsData<>(
			"200-10",
			"Playlist 트랙 조회 성공",
			response
		);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "모든 음악 목록 조회",
		description = "현재 DB에 저장된 모든 음악 정보를 조회"
	)
	public RsData<List<MusicResponse>> getAllMusic() {
		List<MusicResponse> musicList = musicService.getAllMusic().stream()
			.map(MusicResponse::fromEntity)
			.toList();
		return new RsData<>(
			"200-4",
			"전체 음악 조회 성공",
			musicList
		);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "특정 ID의 음악 조회",
		description = "DB에서 특정 ID에 해당하는 음악 정보를 반환"
	)
	public RsData<MusicResponse> getMusicById(
		@PathVariable String id
	) {
		Music music = musicService.getMusicById(id);
		return new RsData<>(
			"200-5",
			"ID로 음악 조회 성공",
			MusicResponse.fromEntity(music)
		);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(
		summary = "특정 ID의 음악 삭제",
		description = "DB에서 특정 ID에 해당하는 음악 정보를 삭제"
	)
	public RsData<String> deleteMusic(
		@PathVariable String id
	) {
		musicService.deleteMusic(id);
		return new RsData<>(
			"204-1",
			"음악 삭제 완료",
			null
		);
	}

	@GetMapping("/recent/random/{userId}")
	@Operation(
		summary = "최근에 추가된 음악 중 랜덤한 곡 반환",
		description = "특정 사용자의 최근 추가된 음악 중 랜덤으로 선택하여 반환"
	)
	public RsData<MusicResponse> getRandomRecentMusic(
		@PathVariable String userId
	) {
		Music randomMusic = musicService.getRandomRecentMusic(userId)
			.orElseGet(() -> new Music("", "", "", "", null, "", ""));
		return new RsData<>(
			"200-6",
			"랜덤 음악 조회 성공",
			MusicResponse.fromEntity(randomMusic)
		);

	}
}