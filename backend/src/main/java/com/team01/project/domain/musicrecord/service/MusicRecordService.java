package com.team01.project.domain.musicrecord.service;

import static com.team01.project.global.permission.CalendarPermission.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.calendardate.repository.CalendarDateRepository;
import com.team01.project.domain.music.entity.Music;
import com.team01.project.domain.music.repository.MusicRepository;
import com.team01.project.domain.musicrecord.entity.MusicRecord;
import com.team01.project.domain.musicrecord.entity.MusicRecordId;
import com.team01.project.domain.musicrecord.repository.MusicRecordRepository;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.global.exception.PermissionDeniedException;
import com.team01.project.global.permission.CalendarPermission;
import com.team01.project.global.permission.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MusicRecordService {

	private final MusicRecordRepository musicRecordRepository;
	private final CalendarDateRepository calendarDateRepository;
	private final MusicRepository musicRepository;
	private final UserRepository userRepository;
	private final PermissionService permissionService;

	/**
	 * 캘린더에 기록된 음악 리스트 조회
	 *
	 * @param calendarDateId 캘린더 아이디
	 * @return 음악 리스트
	 */
	public List<Music> findMusicsByCalendarDateId(Long calendarDateId) {
		CalendarDate calendarDate = calendarDateRepository.findByIdOrThrow(calendarDateId);

		return musicRecordRepository.findByCalendarDate(calendarDate)
			.stream().map(MusicRecord::getMusic).toList();
	}

	/**
	 * 캘린더에 기록된 음악 기록 하나 조회
	 *
	 * @param calendarDateId 캘린더 아이디
	 * @return 음악 기록
	 */
	public Optional<MusicRecord> findOneByCalendarDateId(Long calendarDateId) {
		CalendarDate calendarDate = calendarDateRepository.findByIdOrThrow(calendarDateId);

		return musicRecordRepository.findTopByCalendarDate(calendarDate);
	}

	/**
	 * 캘린더에 음악 기록 저장
	 *
	 * @param calendarDateId 캘린더 아이디
	 * @param newMusicIds    기록할 전체 음악 아이디 리스트
	 */
	public void createMusicRecords(Long calendarDateId, List<String> newMusicIds) {
		CalendarDate calendarDate = calendarDateRepository.findByIdOrThrow(calendarDateId);

		// 기록할 MusicId 목록
		Set<String> newMusicIdSet = new HashSet<>(newMusicIds);

		List<MusicRecord> musicRecordsToAdd = newMusicIdSet.stream()
			.map(musicId -> new MusicRecord(
				new MusicRecordId(calendarDateId, musicId),
				calendarDate,
				musicRepository.findByIdOrThrow(musicId)
			))
			.toList();

		musicRecordRepository.saveAll(musicRecordsToAdd);
	}

	/**
	 * 캘린더 음악 기록 수정
	 *
	 * @param calendarDateId 캘린더 아이디
	 * @param loggedInUserId 현재 인증된 유저
	 * @param newMusicIds    기록할 전체 음악 아이디 리스트
	 */
	public void updateMusicRecords(Long calendarDateId, String loggedInUserId, List<String> newMusicIds) {
		CalendarDate calendarDate = calendarDateRepository.findWithOwnerByIdOrThrow(calendarDateId);
		User calendarOwner = calendarDate.getUser();
		User loggedInUser = userRepository.getById(loggedInUserId);

		CalendarPermission calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser);

		if (EDIT != calendarPermission) {
			throw new PermissionDeniedException("403-12", "캘린더를 수정할 권한이 없습니다.");
		}

		// 1. 기존 MusicRecord 조회
		List<MusicRecord> oldMusicRecords = musicRecordRepository.findByCalendarDate(calendarDate);

		// 2. 기존 MusicId 목록 조회
		Set<String> oldMusicIdset = oldMusicRecords.stream()
			.map(musicRecord -> musicRecord.getMusic().getId())
			.collect(Collectors.toSet());

		// 3. 새로 추가될 MusicId 목록
		Set<String> newMusicIdSet = new HashSet<>(newMusicIds);

		// 4. 삭제할 MusicRecord 목록
		List<MusicRecord> musicRecordsToDelete = oldMusicRecords.stream()
			.filter(musicRecord -> !newMusicIdSet.contains(musicRecord.getMusic().getId()))
			.toList();

		// 5. 추가할 MusicRecord 목록
		List<MusicRecord> musicRecordsToAdd = newMusicIdSet.stream()
			.filter(musicId -> !oldMusicIdset.contains(musicId))
			.map(musicId -> new MusicRecord(
				new MusicRecordId(calendarDateId, musicId),
				calendarDate,
				musicRepository.findByIdOrThrow(musicId)
			))
			.toList();

		// 6. MusicRecord 업데이트
		musicRecordRepository.deleteAll(musicRecordsToDelete);
		musicRecordRepository.saveAll(musicRecordsToAdd);
	}

	/**
	 * 특정 연도와 월에 기록한 MusicRecord 리스트 조회
	 */
	public List<MusicRecord> getMusicRecordsByUserAndDateRange(String userId, LocalDate startDate, LocalDate endDate) {
		return musicRecordRepository.findMusicRecordsByUserAndDateRange(userId, startDate, endDate);
	}

}