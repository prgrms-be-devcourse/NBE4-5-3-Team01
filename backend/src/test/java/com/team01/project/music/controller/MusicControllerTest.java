//package com.team01.project.music.controller;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.team01.project.domain.music.controller.MusicController;
//import com.team01.project.domain.music.dto.MusicRequest;
//import com.team01.project.domain.music.dto.MusicResponse;
//import com.team01.project.domain.music.entity.Music;
//import com.team01.project.domain.music.service.MusicService;
//import com.team01.project.domain.music.service.SpotifyService;
//
//@ExtendWith(MockitoExtension.class)
//public class MusicControllerTest {
//
//	private MockMvc mvc;
//
//	@Mock
//	private MusicService musicService;
//
//	@Mock
//	private SpotifyService spotifyService;
//
//	@InjectMocks
//	private MusicController musicController;
//
//	private String token;
//	private List<Music> musicList;
//
//	@BeforeEach
//	void setUp() {
//		mvc = MockMvcBuilders.standaloneSetup(musicController).build();
//
//		// 토큰 값 입력
//		token = "your-token";
//
//		// 테스트용 음악 데이터 저장
//		musicList = Arrays.asList(
//			new Music("1R0hxCA5R7z5TiaXBZR7Mf", "SOLO", "JENNIE", LocalDate.parse("2018-11-12"),
//				"https://i.scdn.co/image/ab67616d0000b273d0b43791d31a569726a34064", "k-pop"),
//			new Music("1SS0WlKhJewviwEDZ6dWj0", "SPOT!", "ZICO, JENNIE", LocalDate.parse("2024-04-26"),
//				"https://i.scdn.co/image/ab67616d0000b2731b8ae147aceb9fc130391287", "k-rap, k-pop"),
//			new Music("30HIJzJEUYcL9Qng15UeBo", "toxic till the end", "ROSÉ", LocalDate.parse("2024-12-06"),
//				"https://i.scdn.co/image/ab67616d0000b273a9fb6e00986e42ad4764b1f3", "k-pop")
//		);
//	}
//
//	@Test
//	@DisplayName("Spotify API에서 특정 음악 정보 조회")
//	void testGetMusicFromSpotify() throws Exception {
//		// given - 테스트 데이터 설정
//		Music testMusic = musicList.get(1);
//		MusicRequest musicRequest = new MusicRequest(
//			testMusic.getId(),
//			testMusic.getName(),
//			testMusic.getSinger(),
//			testMusic.getReleaseDate(),
//			testMusic.getAlbumImage(),
//			testMusic.getGenre()
//		);
//
//		// SpotifyService Mock 설정
//		when(spotifyService.getTrackWithGenre(eq(musicRequest.id()), any())).thenReturn(musicRequest);
//
//		// 컨트롤러에서 변환되는 응답 예상
//		MusicResponse expectedResponse = MusicResponse.fromEntity(musicRequest.toEntity());
//
//		// when - 실제 요청 실행
//		ResultActions resultActions = mvc.perform(
//			get("/music/spotify/" + musicRequest.id())
//				.header("Authorization", "Bearer " + token))
//			.andDo(print());
//
//		// then - 결과 검증
//		resultActions
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.id").value(expectedResponse.id()))
//			.andExpect(jsonPath("$.name").value(expectedResponse.name()))
//			.andExpect(jsonPath("$.singer").value(expectedResponse.singer()))
//			.andExpect(jsonPath("$.releaseDate").value(expectedResponse.releaseDate().toString()))
//			.andExpect(jsonPath("$.albumImage").value(expectedResponse.albumImage()))
//			.andExpect(jsonPath("$.genre").value(expectedResponse.genre()));
//	}
//
//	@Test
//	@DisplayName("Spotify API에서 음악 정보를 가져와 저장")
//	void testSaveMusicFromSpotify() throws Exception {
//		// given - 테스트 데이터 설정
//		MusicRequest musicRequest = new MusicRequest(
//			"6uPnrBgweGOcwjFL4ItAvV",
//			"Whiplash",
//			"aespa",
//			LocalDate.of(2024, 10, 21),
//			"https://i.scdn.co/image/ab67616d0000b273e467a8e8d7b0aa92d354aa75",
//			"k-pop"
//		);
//
//		// SpotifyService Mock 설정
//		when(spotifyService.getTrackWithGenre(eq(musicRequest.id()), any())).thenReturn(musicRequest);
//
//		// 컨트롤러에서 변환되는 예상 데이터
//		Music expectedMusic = musicRequest.toEntity();
//
//		// musicService.saveMusic() Mock 설정
//		when(musicService.saveMusic(any())).thenReturn(expectedMusic);
//
//		// 컨트롤러에서 예상 응답 DTO 리턴
//		MusicResponse expectedResponse = MusicResponse.fromEntity(expectedMusic);
//
//		// when - 실제 요청 실행
//		ResultActions resultActions = mvc.perform(
//			post("/music/spotify/" + musicRequest.id())
//				.header("Authorization", "Bearer " + token))
//			.andDo(print());
//
//		// then - 결과 검증
//		resultActions
//			.andExpect(status().isCreated())
//			.andExpect(jsonPath("$.id").value(expectedResponse.id()))
//			.andExpect(jsonPath("$.name").value(expectedResponse.name()))
//			.andExpect(jsonPath("$.singer").value(expectedResponse.singer()))
//			.andExpect(jsonPath("$.releaseDate").value(expectedResponse.releaseDate().toString()))
//			.andExpect(jsonPath("$.albumImage").value(expectedResponse.albumImage()))
//			.andExpect(jsonPath("$.genre").value(expectedResponse.genre()));
//	}
//
//	@Test
//	@DisplayName("Spotify API에서 키워드로 음악 검색")
//	void testSearchTracks() throws Exception {
//		// given - 테스트 데이터 설정
//		String keyword = "chill";
//		List<MusicRequest> searchResults = Arrays.asList(
//			new MusicRequest("1QIUF20HdqMA0CJvkBOHNb", "Chill", "LISA", LocalDate.parse("2025-02-28"),
//				"https://i.scdn.co/image/ab67616d0000b2738034090e4afb5b053cd3e067", "k-pop"),
//			new MusicRequest("24QnH4LamDh2UhhmHyXjE8", "Hinoki Wood", "Gia Margaret", LocalDate.parse("2023-05-26"),
//				"https://i.scdn.co/image/ab67616d0000b273abc81056347f57e2f048b452", ""),
//			new MusicRequest("4YUiJ6Av2Hp1hiWE9eeAjO", "Chill Kill", "Red Velvet", LocalDate.parse("2023-11-13"),
//				"https://i.scdn.co/image/ab67616d0000b273d907428ecc02da4077c208d4", "k-pop"),
//			new MusicRequest("0qpdzfTxAkOREtvvGO5oew", "Chill Baby", "SZA", LocalDate.parse("2024-12-20"),
//				"https://i.scdn.co/image/ab67616d0000b2737f5a318e3ff35defa8d0e4af", "r&b"),
//			new MusicRequest("4ppKM7xnkSAwSyKqD4QTY4", "Chill Bae", "Lil Uzi Vert", LocalDate.parse("2024-11-01"),
//				"https://i.scdn.co/image/ab67616d0000b2730e4e16d910115fead3e83496", "melodic rap")
//		);
//
//		// SpotifyService Mock 설정
//		when(spotifyService.searchByKeyword(eq(keyword), any())).thenReturn(searchResults);
//
//		// when - 실제 요청 실행
//		ResultActions resultActions = mvc.perform(
//				get("/music/spotify/search").param("keyword", keyword)
//					.header("Authorization", "Bearer " + token))
//			.andDo(print());
//
//		// then - 결과 검증
//		resultActions
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.size()").value(searchResults.size()));
//
//		for (int i = 0; i < searchResults.size(); i++) {
//			resultActions.andExpect(jsonPath("$[" + i + "].id").value(searchResults.get(i).id()));
//		}
//	}
//
//	@Test
//	@DisplayName("Spotify API에서 특정 아티스트의 인기 곡 조회 - 정상 응답 검증")
//	void testGetTopTracksByArtist() throws Exception {
//		// given - 테스트 데이터 설정
//		String artistId = "6YVMFz59CuY7ngCxTxjpxE";
//		List<MusicRequest> topTracks = Arrays.asList(
//			new MusicRequest("6uPnrBgweGOcwjFL4ItAvV", "Whiplash", "aespa", LocalDate.parse("2024-10-21"), "https://i.scdn.co/image/ab67616d0000b273e467a8e8d7b0aa92d354aa75", "k-pop"),
//			new MusicRequest("2VdSktBqFfkW7y6q5Ik4Z4", "Supernova", "aespa", LocalDate.parse("2024-05-27"), "https://i.scdn.co/image/ab67616d0000b2730fc598038040859794c600e2", "k-pop"),
//			new MusicRequest("5sjnkOfTLCLNfkkchI2re2", "UP - KARINA Solo", "aespa", LocalDate.parse("2024-10-09"), "https://i.scdn.co/image/ab67616d0000b273253096eda3b7826c11c7fab8", "k-pop"),
//			new MusicRequest("5XWlyfo0kZ8LF7VSyfS4Ew", "Drama", "aespa", LocalDate.parse("2023-11-10"), "https://i.scdn.co/image/ab67616d0000b273c54e39f2ae0dd10731f93c08", "k-pop"),
//			new MusicRequest("5eWcGfUCrVFMoYskyfkEPE", "Armageddon", "aespa", LocalDate.parse("2024-05-27"), "https://i.scdn.co/image/ab67616d0000b2730fc598038040859794c600e2", "k-pop")
//		);
//
//		// SpotifyService Mock 설정
//		when(spotifyService.getTopTracksByArtist(eq(artistId), any())).thenReturn(topTracks);
//
//		// when - 실제 요청 실행
//		ResultActions resultActions = mvc.perform(
//				get("/music/spotify/artist/" + artistId + "/top-tracks")
//					.header("Authorization", "Bearer " + token))
//			.andDo(print());
//
//		// then - 결과 검증
//		resultActions
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.size()").value(topTracks.size()));
//
//		for (int i = 0; i < topTracks.size(); i++) {
//			resultActions.andExpect(jsonPath("$[" + i + "].id").value(topTracks.get(i).id()));
//		}
//	}
//
//	@Test
//	@DisplayName("Spotify API에서 특정 아티스트의 인기 곡 조회 - 결과 없음")
//	void testGetTopTracksByArtistNoResults() throws Exception {
//		// given - 테스트 데이터 설정
//		String artistId = "aespa";
//		List<MusicRequest> topTracks = Collections.emptyList();
//
//		// SpotifyService Mock 설정
//		when(spotifyService.getTopTracksByArtist(eq(artistId), any())).thenReturn(topTracks);
//
//		// when - 실제 요청 실행
//		ResultActions resultActions = mvc.perform(
//				get("/music/spotify/artist/" + artistId + "/top-tracks")
//					.header("Authorization", "Bearer " + token))
//			.andDo(print());
//
//		// then - 결과 검증
//		resultActions
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.size()").value(0));
//	}
//
//	@Test
//	@DisplayName("저장된 모든 음악 조회")
//	void testGetAllMusic() throws Exception {
//		// given - 테스트 데이터 설정
//		List<Music> expectedMusicList = musicList;
//
//		// MusicResponse 리스트로 변환
//		List<MusicResponse> expectedResponses = expectedMusicList.stream()
//			.map(MusicResponse::fromEntity)
//			.collect(Collectors.toList());
//
//		// musicService.getAllMusic() Mock 설정
//		when(musicService.getAllMusic()).thenReturn(expectedMusicList);
//
//		// when - 실제 요청 실행
//		ResultActions resultActions = mvc
//			.perform(get("/music"))
//			.andDo(print());
//
//		// then - 결과 검증
//		resultActions
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.size()").value(expectedResponses.size()))
//			.andExpect(jsonPath("$[0].id").value(expectedResponses.get(0).id()))
//			.andExpect(jsonPath("$[1].id").value(expectedResponses.get(1).id()));
//	}
//
//	@Test
//	@DisplayName("ID로 특정 음악 조회")
//	void testGetMusicById() throws Exception {
//		// given - 테스트 데이터 설정
//		Music expectedMusic = musicList.get(2);
//		String id = expectedMusic.getId();
//
//		// musicService.getMusicById() Mock 설정
//		when(musicService.getMusicById(eq(id))).thenReturn(expectedMusic);
//
//		// MusicResponse 변환
//		MusicResponse expectedResponse = MusicResponse.fromEntity(expectedMusic);
//
//		// when - 실제 요청 실행
//		ResultActions resultActions = mvc
//			.perform(get("/music/" + id))
//			.andDo(print());
//
//		// then - 결과 검증
//		resultActions
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.id").value(expectedResponse.id()))
//			.andExpect(jsonPath("$.name").value(expectedResponse.name()))
//			.andExpect(jsonPath("$.singer").value(expectedResponse.singer()))
//			.andExpect(jsonPath("$.releaseDate").value(expectedResponse.releaseDate().toString()))
//			.andExpect(jsonPath("$.albumImage").value(expectedResponse.albumImage()))
//			.andExpect(jsonPath("$.genre").value(expectedResponse.genre()));
//	}
//
//	@Test
//	@DisplayName("ID로 음악 삭제")
//	void test5() throws Exception {
//		// given - 테스트 데이터 설정
//		String id = musicList.get(0).getId();
//
//		// musicService.deleteMusic() Mock 설정
//		doNothing().when(musicService).deleteMusic(eq(id));
//
//		// when - 실제 요청 실행
//		ResultActions resultActions = mvc
//			.perform(delete("/music/" + id))
//			.andDo(print());
//
//		// then - 결과 검증
//		resultActions
//			.andExpect(status().isNoContent());
//	}
//}
