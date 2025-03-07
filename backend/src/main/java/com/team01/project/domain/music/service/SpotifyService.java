package com.team01.project.domain.music.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.team01.project.domain.music.dto.MusicRequest;
import com.team01.project.domain.music.dto.SpotifyArtistResponse;
import com.team01.project.domain.music.dto.SpotifyTrackResponse;

@Service
public class SpotifyService {

	private final WebClient webClient;

	public SpotifyService() {
		this.webClient = WebClient.builder().baseUrl("https://api.spotify.com/v1").build();
	}

	public SpotifyTrackResponse getTrackInfo(String trackId, String accessToken) {
		String url = "/tracks/" + trackId + "?market=KO";
		String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

		return webClient.get()
			.uri(url)
			.headers(headers -> headers.setBearerAuth(token))
			.retrieve()
			.bodyToMono(SpotifyTrackResponse.class)
			.block();
	}

	public List<String> getArtistGenres(String artistId, String accessToken) {
		String url = "/artists/" + artistId;
		String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

		SpotifyArtistResponse response = webClient.get()
			.uri(url)
			.headers(headers -> headers.setBearerAuth(token))
			.retrieve()
			.bodyToMono(SpotifyArtistResponse.class)
			.block();

		return response != null ? response.getGenres() : List.of();
	}

	public MusicRequest getTrackWithGenre(String trackId, String accessToken) {
		SpotifyTrackResponse track = getTrackInfo(trackId, accessToken);
		if (track == null) {
			return null;
		}

		List<String> artistIds = track.getArtists().stream()
			.map(SpotifyTrackResponse.Artist::getId)
			.collect(Collectors.toList());

		Set<String> allGenres = artistIds.stream()
			.flatMap(id -> getArtistGenres(id, accessToken).stream())
			.collect(Collectors.toSet());

		return new MusicRequest(
			track.getName(),
			track.getArtistsAsString(),
			LocalDate.parse(track.getAlbum().getReleaseDate(), DateTimeFormatter.ISO_DATE),
			track.getAlbum().getImages().get(0).getUrl(),
			String.join(", ", allGenres)
		);
	}
}
