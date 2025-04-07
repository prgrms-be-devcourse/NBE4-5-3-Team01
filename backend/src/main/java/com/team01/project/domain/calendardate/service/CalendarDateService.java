package com.team01.project.domain.calendardate.service;

import static com.team01.project.global.permission.CalendarPermission.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.calendardate.controller.dto.response.CalendarDateFetchResponse;
import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.calendardate.repository.CalendarDateRepository;
import com.team01.project.domain.music.entity.Music;
import com.team01.project.domain.musicrecord.service.MusicRecordService;
import com.team01.project.domain.notification.event.NotificationRecordEvent;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.global.exception.CalendarDateAlreadyExistsException;
import com.team01.project.global.exception.PermissionDeniedException;
import com.team01.project.global.permission.CalendarPermission;
import com.team01.project.global.permission.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarDateService {

	private final CalendarDateRepository calendarDateRepository;
	private final UserRepository userRepository;
	private final PermissionService permissionService;
	private final ApplicationEventPublisher eventPublisher;    // 🔥 이벤트 발행기 추가
	private final MusicRecordService musicRecordService;

	/**
	 * 특정 연도와 월에 해당하는 캘린더 리스트 조회
	 */
	public List<CalendarDate> findAllByYearAndMonth(String calendarOwnerId, String loggedInUserId,
		YearMonth yearMonth) {
		LocalDate start = yearMonth.atDay(1);
		LocalDate end = yearMonth.atEndOfMonth();

		User calendarOwner = userRepository.getById(calendarOwnerId);
		User loggedInUser = userRepository.getById(loggedInUserId);

		CalendarPermission calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser);

		if (NONE == calendarPermission) {
			throw new PermissionDeniedException("403-10", "먼슬리 캘린더를 조회할 권한이 없습니다.");
		}

		return calendarDateRepository.findByUserAndDateBetween(calendarOwner, start, end);
	}

	/**
	 * 캘린더 및 음악 기록 조회
	 */
	public CalendarDateFetchResponse findCalendarDateWithMusics(Long calendarDateId, String loggedInUserId) {
		CalendarDate calendarDate = calendarDateRepository.findWithOwnerByIdOrThrow(calendarDateId);
		User calendarOwner = calendarDate.getUser();
		User loggedInUser = userRepository.getById(loggedInUserId);

		CalendarPermission calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser);

		if (NONE == calendarPermission) {
			throw new PermissionDeniedException("403-11", "캘린더를 조회할 권한이 없습니다.");
		}

		List<Music> musics = musicRecordService.findMusicsByCalendarDateId(calendarDateId);

		return CalendarDateFetchResponse.of(calendarDate, musics, calendarPermission);
	}

	/**
	 * 캘린더 생성
	 */
	public CalendarDate create(String userId, LocalDate date, String memo) {
		User user = userRepository.getById(userId);

		if (calendarDateRepository.existsByUserAndDate(user, date)) {
			throw new CalendarDateAlreadyExistsException("409-10", "해당 날짜의 캘린더가 이미 존재합니다.");
		}

		// 🔥 이벤트 발행 (`NotificationScheduler`에서 감지할 수 있도록) 캘린더 생성 알림
		eventPublisher.publishEvent(new NotificationRecordEvent(this, LocalTime.now(), user));

		CalendarDate calendarDate = CalendarDate.builder()
			.user(user)
			.date(date)
			.memo(memo)
			.build();

		return calendarDateRepository.save(calendarDate);
	}

	/**
	 * 메모 수정
	 */
	public void updateMemo(Long calendarDateId, String loggedInUserId, String memo) {
		CalendarDate calendarDate = calendarDateRepository.findWithOwnerByIdOrThrow(calendarDateId);
		User calendarOwner = calendarDate.getUser();
		User loggedInUser = userRepository.getById(loggedInUserId);

		CalendarPermission calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser);

		if (EDIT != calendarPermission) {
			throw new PermissionDeniedException("403-12", "캘린더를 수정할 권한이 없습니다.");
		}

		calendarDate.writeMemo(memo);
	}

	/**
	 * 오늘 날짜 캘린더 조회
	 */
	public Optional<CalendarDate> findByUserIdAndDate(String userId, LocalDate date) {
		return calendarDateRepository.findByUserIdAndDate(userId, date);
	}

}