package com.team01.project.domain.music.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.team01.project.domain.music.dto.SpotifyPlaylistResponse;
import com.team01.project.domain.music.dto.SpotifyTrackResponse;
import com.team01.project.global.exception.SpotifyApiException;

import reactor.core.publisher.Mono;

@Service
public class SpotifyService {

	private final WebClient webClient;
	private final ObjectMapper objectMapper;
	private final Map<String, List<String>> genreCache = new HashMap<>();

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
		if (genreCache.containsKey(artistId)) {
			return genreCache.get(artistId);
		}

		String url = "/artists/" + artistId;
		String token = extractToken(accessToken);

		try {
			SpotifyArtistResponse response = webClient.get()
				.uri(url)
				.headers(headers -> headers.setBearerAuth(token))
				.retrieve()
				.bodyToMono(SpotifyArtistResponse.class)
				.block();

			List<String> genres = response != null ? response.getGenres() : List.of();
			genreCache.put(artistId, genres);
			return genres;
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

		LocalDate parsedReleaseDate = parseReleaseDate(track.getAlbum().getReleaseDate());

		return new MusicRequest(
			track.getId(),
			track.getName(),
			track.getArtistsAsString(),
			track.getArtistsIdAsString(),
			parsedReleaseDate,
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

			List<MusicRequest> musicRequests = new ArrayList<>();
			Set<String> artistIds = new HashSet<>();

			for (JsonNode item : items) {
				SpotifyTrackResponse track = objectMapper.treeToValue(item, SpotifyTrackResponse.class);
				artistIds.addAll(
					track.getArtists().stream().map(SpotifyTrackResponse.Artist::getId).collect(Collectors.toSet()));
				LocalDate parsedReleaseDate = parseReleaseDate(track.getAlbum().getReleaseDate());

				musicRequests.add(new MusicRequest(
					track.getId(),
					track.getName(),
					track.getArtistsAsString(),
					track.getArtistsIdAsString(),
					parsedReleaseDate,
					track.getAlbum().getImages().get(0).getUrl(),
					null
				));
			}

			return musicRequests;

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

			List<MusicRequest> musicRequests = new ArrayList<>();

			for (JsonNode trackNode : tracks) {
				SpotifyTrackResponse track = objectMapper.treeToValue(trackNode, SpotifyTrackResponse.class);

				LocalDate parsedReleaseDate = parseReleaseDate(track.getAlbum().getReleaseDate());
				musicRequests.add(new MusicRequest(
					track.getId(),
					track.getName(),
					track.getArtistsAsString(),
					track.getArtistsIdAsString(),
					parsedReleaseDate,
					track.getAlbum().getImages().get(0).getUrl(),
					null
				));
			}

			return musicRequests;

		} catch (WebClientResponseException e) {
			throw new SpotifyApiException("Spotify API 요청 오류: " + e.getResponseBodyAsString(), e);
		} catch (Exception e) {
			throw new SpotifyApiException("알 수 없는 오류 발생: " + e.getMessage(), e);
		}
	}

	public List<SpotifyPlaylistResponse> getUserPlaylists(String accessToken) {
		String url = "/me/playlists?limit=10";
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
			JsonNode items = root.path("items");

			List<SpotifyPlaylistResponse> result = new ArrayList<>();

			for (JsonNode item : items) {
				String id = item.path("id").asText();
				String name = item.path("name").asText();
				String image = item.path("images").isArray() && item.path("images").size() > 0
					? item.path("images").get(0).path("url").asText()
					: null;
				int trackCount = item.get("tracks").path("total").asInt();

				result.add(new SpotifyPlaylistResponse(id, name, image, trackCount));
			}

			return result;

		} catch (Exception e) {
			throw new SpotifyApiException("사용자의 Playlist 목록 조회 오류: " + e.getMessage(), e);
		}
	}

	public List<MusicRequest> getTracksFromPlaylist(String playlistId, String accessToken) {
		String url = "/playlists/" + playlistId + "/tracks?market=KR";
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

			JsonNode items = objectMapper.readTree(jsonResponse).path("items");
			List<MusicRequest> result = new ArrayList<>();

			for (JsonNode item : items) {
				JsonNode trackNode = item.path("track");
				if (trackNode.isMissingNode() || trackNode.isNull())
					continue;

				SpotifyTrackResponse track = objectMapper.treeToValue(trackNode, SpotifyTrackResponse.class);
				LocalDate parsedReleaseDate = parseReleaseDate(track.getAlbum().getReleaseDate());

				result.add(new MusicRequest(
					track.getId(),
					track.getName(),
					track.getArtistsAsString(),
					track.getArtistsIdAsString(),
					parsedReleaseDate,
					track.getAlbum().getImages().get(0).getUrl(),
					null
				));
			}

			return result;

		} catch (Exception e) {
			throw new SpotifyApiException("Playlist 트랙 조회 실패: " + e.getMessage(), e);
		}
	}

	private LocalDate parseReleaseDate(String releaseDate) {
		if (releaseDate == null || releaseDate.isBlank()) {
			return null; // 날짜가 없으면 null 반환
		}

		try {
			if (releaseDate.length() == 4) {  // "yyyy"
				return LocalDate.of(Integer.parseInt(releaseDate), 1, 1);
			} else if (releaseDate.length() == 7) {  // "yyyy-MM"
				return LocalDate.parse(releaseDate + "-01", DateTimeFormatter.ISO_DATE);
			} else {  // "yyyy-MM-dd"
				return LocalDate.parse(releaseDate, DateTimeFormatter.ISO_DATE);
			}
		} catch (Exception e) {
			System.err.println("날짜 변환 오류: " + releaseDate);
			return null; // 오류 발생 시 null 반환
		}
	}

	// private Map<String, String> fetchArtistGenres(Set<String> artistIds, String accessToken) {
	// 	String token = extractToken(accessToken);
	// 	Map<String, String> artistGenres = new HashMap<>();
	//
	// 	List<Mono<Map.Entry<String, String>>> requests = artistIds.stream()
	// 		.map(artistId -> webClient.get()
	// 			.uri("/artists/" + artistId)
	// 			.headers(headers -> headers.setBearerAuth(token))
	// 			.retrieve()
	// 			.bodyToMono(SpotifyArtistResponse.class)
	// 			.map(response -> Map.entry(artistId, String.join(", ", response.getGenres())))
	// 			.onErrorResume(e -> {
	// 				System.err.println("아티스트 ID: " + artistId + " 장르 조회 실패: " + e.getMessage());
	// 				return Mono.empty();
	// 			}))
	// 		.collect(Collectors.toList());
	//
	// 	// 병렬 요청 처리 후 결과 매핑
	// 	List<Map.Entry<String, String>> results = Mono.zip(requests, objects ->
	// 			Arrays.stream(objects)
	// 				.map(o -> (Map.Entry<String, String>)o)
	// 				.collect(Collectors.toList()))
	// 		.block();
	//
	// 	if (results != null) {
	// 		results.forEach(entry -> artistGenres.put(entry.getKey(), entry.getValue()));
	// 	}
	//
	// 	return artistGenres;
	// }
}
