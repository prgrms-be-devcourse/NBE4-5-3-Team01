package com.team01.project.musicrecord.service;

import static com.team01.project.global.permission.CalendarPermission.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.calendardate.repository.CalendarDateRepository;
import com.team01.project.domain.music.entity.Music;
import com.team01.project.domain.music.repository.MusicRepository;
import com.team01.project.domain.musicrecord.entity.MusicRecord;
import com.team01.project.domain.musicrecord.entity.MusicRecordId;
import com.team01.project.domain.musicrecord.repository.MusicRecordRepository;
import com.team01.project.domain.musicrecord.service.MusicRecordService;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.global.permission.PermissionService;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MusicRecordServiceTest {

	@Mock
	private MusicRecordRepository musicRecordRepository;

	@Mock
	private CalendarDateRepository calendarDateRepository;

	@Mock
	private MusicRepository musicRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PermissionService permissionService;

	@InjectMocks
	private MusicRecordService musicRecordService;

	private Long calendarDateId;
	private CalendarDate calendarDate;
	private List<MusicRecord> musicRecords;
	private String userId;
	private User user;

	@BeforeEach
	void 초기화() {
		userId = "test-user";
		user = User.builder().id(userId).build();

		calendarDateId = 1L;
		calendarDate = CalendarDate.builder().id(calendarDateId).user(user).build();

		Music music1 = Music.builder().id("1").name("Song 1").build();
		Music music2 = Music.builder().id("2").name("Song 2").build();

		MusicRecord record1 = new MusicRecord(new MusicRecordId(calendarDateId, "1"), calendarDate, music1);
		MusicRecord record2 = new MusicRecord(new MusicRecordId(calendarDateId, "2"), calendarDate, music2);

		musicRecords = List.of(record1, record2);
	}

	@Test
	void 기록한_음악_목록을_캘린더_아이디로_조회한다() {

		// given
		when(calendarDateRepository.findByIdOrThrow(calendarDateId)).thenReturn(calendarDate);
		when(musicRecordRepository.findByCalendarDate(calendarDate)).thenReturn(musicRecords);

		// when
		List<Music> result = musicRecordService.findMusicsByCalendarDateId(calendarDateId);

		// then
		assertThat(result).hasSize(musicRecords.size());
		assertThat(result.stream().map(Music::getId).collect(Collectors.toSet()))
			.containsExactlyInAnyOrder("1", "2");

	}

	@Test
	void 음악_기록_하나를_캘린더_아이디로_조회한다() {

		// given
		when(calendarDateRepository.findByIdOrThrow(calendarDateId)).thenReturn(calendarDate);
		when(musicRecordRepository.findTopByCalendarDate(calendarDate)).thenReturn(
			Optional.of(musicRecords.getFirst()));

		// when
		Optional<MusicRecord> result = musicRecordService.findOneByCalendarDateId(calendarDateId);

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getMusic().getId()).isEqualTo(musicRecords.getFirst().getMusic().getId());

	}

	@Test
	void 음악_기록을_업데이트한다() {

		// given
		String commonMusicId = musicRecords.getFirst().getMusic().getId();
		String musicIdToAdd = "3";
		String musicNameToAdd = "Song 3";

		when(calendarDateRepository.findWithOwnerByIdOrThrow(calendarDateId)).thenReturn(calendarDate);
		when(userRepository.getById(userId)).thenReturn(user);
		when(permissionService.checkPermission(any(User.class), any(User.class))).thenReturn(EDIT);
		when(musicRecordRepository.findByCalendarDate(calendarDate)).thenReturn(musicRecords);
		when(musicRepository.findByIdOrThrow(musicIdToAdd))
			.thenReturn(Music.builder().id(musicIdToAdd).name(musicNameToAdd).build());

		List<String> newMusicIds = List.of(commonMusicId, musicIdToAdd);

		// when
		musicRecordService.updateMusicRecords(calendarDateId, userId, newMusicIds);

		// then
		verify(musicRecordRepository).deleteAll(any());
		verify(musicRecordRepository).saveAll(any());

	}

}