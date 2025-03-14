package com.team01.project.domain.music.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team01.project.domain.music.dto.MusicRequest;
import com.team01.project.domain.music.dto.SpotifyArtistResponse;
import com.team01.project.domain.music.dto.SpotifyTrackResponse;
import com.team01.project.global.exception.SpotifyApiException;

import reactor.core.publisher.Mono;

@Service
public class SpotifyService {

	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	public SpotifyService() {
		this.webClient = WebClient.builder()
			.baseUrl("https://api.spotify.com/v1")
			.filter(errorHandlingFilter())
			.exchangeStrategies(ExchangeStrategies.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
				.build())
			.build();
		this.objectMapper = new ObjectMapper();
	}

	private String extractToken(String accessToken) {
		return accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
	}

	private ExchangeFilterFunction errorHandlingFilter() {
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
			if (clientResponse.statusCode().is5xxServerError()) {
				return Mono.error(new RuntimeException("Spotify API 서버 오류 발생"));
			} else if (clientResponse.statusCode().is4xxClientError()) {
				return Mono.error(new RuntimeException("Spotify API 요청 오류 발생"));
			}
			return Mono.just(clientResponse);
		});
	}

	public SpotifyTrackResponse getTrackInfo(String trackId, String accessToken) {
		String url = "/tracks/" + trackId + "?market=KR";
		String token = extractToken(accessToken);

		try {
			return webClient.get()
				.uri(url)
				.headers(headers -> headers.setBearerAuth(token))
				.retrieve()
				.bodyToMono(SpotifyTrackResponse.class)
				.block();
		} catch (Exception e) {
			throw new SpotifyApiException("트랙 정보를 가져오는 중 오류 발생", e);
		}
	}

	public List<String> getArtistGenres(String artistId, String accessToken) {
		String url = "/artists/" + artistId;
		String token = extractToken(accessToken);

		try {
			SpotifyArtistResponse response = webClient.get()
				.uri(url)
				.headers(headers -> headers.setBearerAuth(token))
				.retrieve()
				.bodyToMono(SpotifyArtistResponse.class)
				.block();

			return response != null ? response.getGenres() : List.of();
		} catch (Exception e) {
			throw new SpotifyApiException("아티스트 장르 정보를 가져오는 중 오류 발생", e);
		}
	}

	public MusicRequest getTrackWithGenre(String trackId, String accessToken) {
		SpotifyTrackResponse track = getTrackInfo(trackId, accessToken);
		if (track == null) {
			throw new SpotifyApiException("트랙 정보를 찾을 수 없습니다.");
		}

		List<String> artistIds = track.getArtists().stream()
			.map(SpotifyTrackResponse.Artist::getId)
			.collect(Collectors.toList());

		Set<String> allGenres = artistIds.stream()
			.flatMap(id -> getArtistGenres(id, accessToken).stream())
			.collect(Collectors.toSet());

		return new MusicRequest(
			track.getId(),
			track.getName(),
			track.getArtistsAsString(),
			LocalDate.parse(track.getAlbum().getReleaseDate(), DateTimeFormatter.ISO_DATE),
			track.getAlbum().getImages().get(0).getUrl(),
			String.join(", ", allGenres)
		);
	}

	public List<MusicRequest> searchByKeyword(String keyword, String accessToken) {
		String url = String.format("/search?q=%s&type=track&limit=10&market=KR", keyword);
		String token = extractToken(accessToken);

		try {
			String jsonResponse = webClient.get()
				.uri(url)
				.headers(headers -> headers.setBearerAuth(token))
				.retrieve()
				.bodyToMono(String.class)
				.block();

			if (jsonResponse == null) {
				throw new SpotifyApiException("Spotify API 응답이 없습니다.");
			}

			JsonNode root = objectMapper.readTree(jsonResponse);
			JsonNode items = root.path("tracks").path("items");

			if (!items.isArray()) {
				throw new SpotifyApiException("Spotify API 응답에서 트랙 정보를 찾을 수 없습니다.");
			}

			List<SpotifyTrackResponse> tracks = new ArrayList<>();

			for (JsonNode item : items) {
				SpotifyTrackResponse track = objectMapper.treeToValue(item, SpotifyTrackResponse.class);
				tracks.add(track);
			}

			return tracks.stream()
				.map(track -> getTrackWithGenre(track.getId(), accessToken))
				.collect(Collectors.toList());

		} catch (WebClientResponseException e) {
			throw new SpotifyApiException("Spotify API 요청 오류: " + e.getResponseBodyAsString(), e);
		} catch (Exception e) {
			throw new SpotifyApiException("검색 결과를 처리하는 중 오류 발생: " + e.getMessage(), e);
		}
	}

	public List<MusicRequest> getTopTracksByArtist(String artistId, String accessToken) {
		String url = "/artists/" + artistId + "/top-tracks?market=KR";
		String token = extractToken(accessToken);

		try {
			String jsonResponse = webClient.get()
				.uri(url)
				.headers(headers -> headers.setBearerAuth(token))
				.retrieve()
				.bodyToMono(String.class)
				.block();

			if (jsonResponse == null) {
				throw new SpotifyApiException("Spotify API 응답이 없습니다.");
			}

			JsonNode root = objectMapper.readTree(jsonResponse);
			JsonNode tracks = root.path("tracks");

			if (!tracks.isArray()) {
				throw new SpotifyApiException("Spotify에서 트랙 정보를 가져오지 못했습니다.");
			}

			List<SpotifyTrackResponse> topTracks = new ArrayList<>();

			for (JsonNode trackNode : tracks) {
				SpotifyTrackResponse topTrack = objectMapper.treeToValue(trackNode, SpotifyTrackResponse.class);
				topTracks.add(topTrack);
			}

			return topTracks.stream()
				// .limit(5)
				.map(track -> getTrackWithGenre(track.getId(), accessToken))
				.collect(Collectors.toList());

		} catch (WebClientResponseException e) {
			throw new SpotifyApiException("Spotify API 요청 오류: " + e.getResponseBodyAsString(), e);
		} catch (Exception e) {
			throw new SpotifyApiException("알 수 없는 오류 발생: " + e.getMessage(), e);
		}
	}
}
