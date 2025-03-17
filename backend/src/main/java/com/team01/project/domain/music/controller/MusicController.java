package com.team01.project.domain.music.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import com.team01.project.domain.music.entity.Music;
import com.team01.project.domain.music.service.MusicService;
import com.team01.project.domain.music.service.SpotifyService;

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
	public MusicResponse getMusicFromSpotify(
		@PathVariable String id,
		@AuthenticationPrincipal OAuth2User user
	) {
		String spotifyToken = user.getAttribute("spotifyToken");
		MusicRequest musicRequest = spotifyService.getTrackWithGenre(id, spotifyToken);
		if (musicRequest != null) {
			Music music = musicRequest.toEntity();
			return MusicResponse.fromEntity(music);
		}
		throw new IllegalArgumentException("Invalid music data");
	}

	@PostMapping("/spotify/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(
		summary = "ID로 Spotify 음악 저장",
		description = "Spotify에서 특정 ID의 음악 정보를 가져와 DB에 저장"
	)
	public void saveMusicFromSpotify(
		@PathVariable String id,
		@AuthenticationPrincipal OAuth2User user
	) {
		String spotifyToken = user.getAttribute("spotifyToken");
		MusicRequest musicRequest = spotifyService.getTrackWithGenre(id, spotifyToken);
		if (musicRequest != null) {
			Music savedMusic = musicService.saveMusic(musicRequest.toEntity());
		}
		throw new IllegalArgumentException("Invalid music data");
	}

	@PostMapping("/save-all")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(
		summary = "음악 리스트 저장",
		description = "요청받은 음악 리스트를 저장하며, 장르 정보가 없을 경우 Spotify에 조회하여 업데이트"
	)
	public void saveAllMusic(
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
			.collect(Collectors.toList());
		musicService.saveAllMusic(updatedMusicList);
	}

	@GetMapping("/spotify/search")
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "키워드로 Spotify에서 음악 검색",
		description = "Spotify에서 특정 키워드로 검색하여 해당되는 음악 리스트 반환"
	)
	public List<MusicResponse> searchTracks(
		@RequestParam String keyword,
		@AuthenticationPrincipal OAuth2User user
	) {
		String spotifyToken = user.getAttribute("spotifyToken");
		List<MusicRequest> tracks = spotifyService.searchByKeyword(keyword, spotifyToken);
		return tracks.stream()
			.map(request -> MusicResponse.fromEntity(request.toEntity()))
			.collect(Collectors.toList());
	}

	@GetMapping("/spotify/artist/{artistId}/top-tracks")
	@Operation(
		summary = "특정 아티스트의 인기곡 조회",
		description = "Spotify에서 특정 아티스트의 인기곡 리스트 반환"
	)
	public List<MusicResponse> getTopTracksByArtist(
		@PathVariable String artistId,
		@AuthenticationPrincipal OAuth2User user
	) {
		String spotifyToken = user.getAttribute("spotifyToken");
		List<MusicRequest> topTracks = spotifyService.getTopTracksByArtist(artistId, spotifyToken);
		return topTracks.stream()
			.map(request -> MusicResponse.fromEntity(request.toEntity()))
			.collect(Collectors.toList());
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "모든 음악 목록 조회",
		description = "현재 DB에 저장된 모든 음악 정보를 조회"
	)
	public List<MusicResponse> getAllMusic() {
		return musicService.getAllMusic().stream()
			.map(MusicResponse::fromEntity)
			.collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "특정 ID의 음악 조회",
		description = "DB에서 특정 ID에 해당하는 음악 정보를 반환"
	)
	public MusicResponse getMusicById(@PathVariable String id) {
		return MusicResponse.fromEntity(musicService.getMusicById(id));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(
		summary = "특정 ID의 음악 삭제",
		description = "DB에서 특정 ID에 해당하는 음악 정보를 삭제"
	)
	public void deleteMusic(@PathVariable String id) {
		musicService.deleteMusic(id);
	}

	@GetMapping("/recent/random/{userId}")
	@Operation(
		summary = "최근에 추가된 음악 중 랜덤한 곡 반환",
		description = "특정 사용자의 최근 추가된 음악 중 랜덤으로 선택하여 반환"
	)
	public MusicResponse getRandomRecentMusic(@PathVariable String userId) {
		Music randomMusic = musicService.getRandomRecentMusic(userId)
			.orElseGet(() -> new Music("", "", "", "", null, "", ""));
		return MusicResponse.fromEntity(randomMusic);
	}
}