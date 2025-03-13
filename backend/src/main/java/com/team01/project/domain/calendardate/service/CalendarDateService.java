package com.team01.project.domain.calendardate.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.calendardate.repository.CalendarDateRepository;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.global.permission.PermissionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarDateService {

	private final CalendarDateRepository calendarDateRepository;
	private final UserRepository userRepository;
	private final PermissionService permissionService;

	/**
	 * 특정 연도와 월에 해당하는 캘린더 리스트 조회
	 */
	public List<CalendarDate> findAllByYearAndMonth(String ownerId, String loggedInUserId, YearMonth yearMonth) {
		LocalDate start = yearMonth.atDay(1);
		LocalDate end = yearMonth.atEndOfMonth();

		User loggedInUser = userRepository.findById(loggedInUserId)
			.orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

		if (ownerId == null) {
			return calendarDateRepository.findByUserAndDateBetween(loggedInUser, start, end);
		}

		User owner = userRepository.findById(ownerId)
			.orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

		permissionService.checkMonthlyFetchPermission(owner, loggedInUser);

		return calendarDateRepository.findByUserAndDateBetween(owner, start, end);
	}

	/**
	 * 캘린더 조회
	 */
	public CalendarDate findById(Long calendarDateId, String loggedInUserId) {
		User loggedInUser = userRepository.findById(loggedInUserId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저입니다."));

		CalendarDate calendarDate = calendarDateRepository.findByIdOrThrow(calendarDateId);

		permissionService.checkCalendarDateFetchPermission(calendarDate, loggedInUser);

		return calendarDate;
	}

	/**
	 * 메모 작성
	 */
	public void writeMemo(Long calendarDateId, String loggedInUserId, String memo) {
		User loggedInUser = userRepository.findById(loggedInUserId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저입니다."));

		CalendarDate calendarDate = calendarDateRepository.findByIdOrThrow(calendarDateId);

		permissionService.checkCalendarDateUpdatePermission(calendarDateId, loggedInUser);

		calendarDate.writeMemo(memo);
	}

	/**
	 * 캘린더 생성
	 */
	public CalendarDate create(String userId, LocalDate date, String memo) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

		if (calendarDateRepository.existsByUserAndDate(user, date)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 날짜의 캘린더가 이미 존재합니다.");
		}

		CalendarDate calendarDate = CalendarDate.builder()
			.user(user)
			.date(date)
			.memo(memo)
			.build();

		return calendarDateRepository.save(calendarDate);
	}

}