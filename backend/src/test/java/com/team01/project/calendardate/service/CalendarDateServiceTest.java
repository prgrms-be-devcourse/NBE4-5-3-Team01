package com.team01.project.calendardate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.when;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.calendardate.repository.CalendarDateRepository;
import com.team01.project.domain.calendardate.service.CalendarDateService;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.global.permission.PermissionService;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CalendarDateServiceTest {

	@Mock
	private CalendarDateRepository calendarDateRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher; // 목 객체

	@Mock
	private PermissionService permissionService;

	@InjectMocks
	private CalendarDateService calendarDateService;

	@Test
	void 먼슬리_캘린더를_유저_아이디와_날짜로_조회한다() {

		// given
		String mockUserId = "test-user";
		User mockUser = User.builder().id(mockUserId).build();

		YearMonth yearMonth = YearMonth.of(2025, 3);
		LocalDate start = yearMonth.atDay(1);
		LocalDate end = yearMonth.atEndOfMonth();

		List<CalendarDate> mockCalendarDates = List.of(
				CalendarDate.builder()
						.user(mockUser)
						.date(LocalDate.of(2025, 3, 1))
						.memo("memo 1")
						.build(),
				CalendarDate.builder()
						.user(mockUser)
						.date(LocalDate.of(2025, 3, 31))
						.memo("memo 2")
						.build()
		);

		when(userRepository.findById(mockUserId)).thenReturn(Optional.ofNullable(mockUser));
		when(calendarDateRepository.findByUserAndDateBetween(mockUser, start, end))
				.thenReturn(mockCalendarDates);

		// when
		List<CalendarDate> result = calendarDateService.findAllByYearAndMonth(mockUserId, mockUserId, yearMonth);

		// then
		assertThat(result).hasSize(mockCalendarDates.size());
		result.forEach(calendarDate ->
				assertThat(calendarDate.getUser().getId()).isEqualTo(mockUserId));

	}

	@Test
	void 캘린더를_아이디로_조회한다() {

		// given
		Long mockCalendarDateId = 1L;
		String mockUserId = "test-user";
		CalendarDate mockCalendarDate = getMockCalendarDate();
		User mockUser = User.builder().id(mockUserId).build();

		when(calendarDateRepository.findByIdOrThrow(mockCalendarDateId)).thenReturn(mockCalendarDate);
		when(userRepository.findById(mockUserId)).thenReturn(Optional.ofNullable(mockUser));

		// when
		CalendarDate result = calendarDateService.findById(mockCalendarDateId, mockUserId);

		// then
		assertNotNull(result);
		assertEquals(mockCalendarDate, result);

	}

	@Test
	void 캘린더에_메모를_작성한다() {

		// given
		Long mockCalendarDateId = 1L;
		String newMemo = "new memo";
		String mockUserId = "test-user";
		LocalDate mockDate = LocalDate.of(2025, 3, 1);
		CalendarDate mockCalendarDate = getMockCalendarDate(mockDate);
		User mockUser = User.builder().id(mockUserId).build();

		when(userRepository.findById(mockUserId)).thenReturn(Optional.ofNullable(mockUser));
		when(calendarDateRepository.findByIdOrThrow(mockCalendarDateId)).thenReturn(mockCalendarDate);

		// when
		calendarDateService.writeMemo(mockCalendarDateId, mockUserId, newMemo);

		// then
		assertEquals(newMemo, mockCalendarDate.getMemo());

	}

	@Test
	void 캘린더를_유저_아이디로_생성한다() {

		// given
		LocalDate mockDate = LocalDate.of(2025, 3, 1);
		String mockMemo = "memo";
		String mockUserId = "test-user";
		User mockUser = User.builder().id(mockUserId).build();

		when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
		when(calendarDateRepository.existsByUserAndDate(mockUser, mockDate)).thenReturn(false);
		when(calendarDateRepository.save(any(CalendarDate.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		CalendarDate result = calendarDateService.create(mockUserId, mockDate, mockMemo);

		// then
		assertNotNull(result);
		assertEquals(mockUserId, result.getUser().getId());
		assertEquals(mockDate, result.getDate());
		assertSame(mockMemo, result.getMemo());

	}

	CalendarDate getMockCalendarDate() {
		return CalendarDate.builder()
				.user(new User())
				.date(LocalDate.of(2025, 3, 1))
				.memo("memo 1")
				.build();
	}

	CalendarDate getMockCalendarDate(LocalDate date) {
		return CalendarDate.builder()
				.user(new User())
				.date(date)
				.memo("memo 1")
				.build();
	}

}