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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

	private final MusicService musicService;
	private final SpotifyService spotifyService;

	@GetMapping("/spotify/{id}")
	@ResponseStatus(HttpStatus.OK)
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
	public List<MusicResponse> getAllMusic() {
		return musicService.getAllMusic().stream()
			.map(MusicResponse::fromEntity)
			.collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public MusicResponse getMusicById(@PathVariable String id) {
		return MusicResponse.fromEntity(musicService.getMusicById(id));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMusic(@PathVariable String id) {
		musicService.deleteMusic(id);
	}

	@GetMapping("/recent/random/{userId}")
	public MusicResponse getRandomRecentMusic(@PathVariable String userId) {
		Music randomMusic = musicService.getRandomRecentMusic(userId)
			.orElseGet(() -> new Music("", "", "", "", null, "", ""));
		return MusicResponse.fromEntity(randomMusic);
	}
}