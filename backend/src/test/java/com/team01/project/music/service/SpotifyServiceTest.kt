//package com.team01.project.music.service;
//
//import com.team01.project.domain.music.dto.MusicRequest;
//import com.team01.project.domain.music.dto.SpotifyTrackResponse;
//import com.team01.project.domain.music.service.SpotifyService;
//import com.team01.project.global.exception.SpotifyApiException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//public class SpotifyServiceTest {
//
//	@Mock
//	private WebClient webClient;
//
//	@InjectMocks
//	private SpotifyService spotifyService;
//
//	private final String accessToken = "access-token";
//	private final String trackId = "30HIJzJEUYcL9Qng15UeBo";
//	private final String artistId = "6YVMFz59CuY7ngCxTxjpxE";
//
//	@BeforeEach
//	void setUp() {
//		spotifyService = new SpotifyService();
//	}
//
//	@Test
//	@DisplayName("getTrackInfoTest - 트랙 정보를 정상적으로 가져옴")
//	void getTrackInfoTest() {
//		SpotifyTrackResponse result = spotifyService.getTrackInfo(trackId, accessToken);
//		assertNotNull(result);
//		assertEquals(trackId, result.getId());
//	}
//
//	@Test
//	@DisplayName("getTrackInfoTest - 잘못된 ID 요청 시 예외를 발생")
//	void getTrackInfoTest_ShouldThrowException() {
//		assertThrows(SpotifyApiException.class, () -> spotifyService.getTrackInfo("wrong_id", accessToken));
//	}
//
//	@Test
//	@DisplayName("아티스트의 장르 정보를 정상적으로 가져옴")
//	void getArtistGenresTest() {
//		List<String> genres = spotifyService.getArtistGenres(artistId, accessToken);
//		assertNotNull(genres);
//		assertFalse(genres.isEmpty());
//	}
//
//	@Test
//	@DisplayName("트랙 정보를 가져오고, 장르 정보를 포함")
//	void getTrackWithGenreTest() {
//		MusicRequest result = spotifyService.getTrackWithGenre(trackId, accessToken);
//		assertNotNull(result);
//		assertEquals(trackId, result.id());
//	}
//
//	@Test
//	@DisplayName("키워드로 검색한 트랙 목록을 가져옴")
//	void searchByKeywordTest() {
//		List<MusicRequest> results = spotifyService.searchByKeyword("chill", accessToken);
//		assertNotNull(results);
//		assertFalse(results.isEmpty());
//	}
//
//	@Test
//	@DisplayName("특정 아티스트의 인기 트랙 목록을 가져옴")
//	void getTopTracksByArtistTest() {
//		List<MusicRequest> results = spotifyService.getTopTracksByArtist(artistId, accessToken);
//		assertNotNull(results);
//		assertFalse(results.isEmpty());
//	}
//}