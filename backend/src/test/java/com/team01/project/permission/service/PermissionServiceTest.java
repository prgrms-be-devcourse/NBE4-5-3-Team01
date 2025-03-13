package com.team01.project.permission.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.calendardate.repository.CalendarDateRepository;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.user.entity.User;
import com.team01.project.global.permission.PermissionService;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PermissionServiceTest {

	@Mock
	private CalendarDateRepository calendarDateRepository;

	@Mock
	private FollowRepository followRepository;

	@InjectMocks
	private PermissionService permissionService;

	private User owner;
	private User loggedInUser;
	private CalendarDate calendarDate;

	@BeforeEach
	void 초기화() {
		owner = User.builder().id("owner").build();
		loggedInUser = User.builder().id("logged in user").build();
		calendarDate = CalendarDate.builder().id(1L).user(owner).build();
	}

	@Test
	void 오너라서_캘린더를_수정할_권한이_있다() {

		// given
		when(calendarDateRepository.existsByIdAndUser(calendarDate.getId(), owner)).thenReturn(true);

		// when & then
		assertThatCode(() -> permissionService.checkCalendarDateUpdatePermission(calendarDate.getId(), owner))
			.doesNotThrowAnyException();

	}

	@Test
	void 오너가_아니라서_캘린더를_수정할_권한이_없다() {

		// given
		when(calendarDateRepository.existsByIdAndUser(calendarDate.getId(), loggedInUser)).thenReturn(false);

		// when & then
		assertThatThrownBy(
			() -> permissionService.checkCalendarDateUpdatePermission(calendarDate.getId(), loggedInUser))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("해당 캘린더를 수정할 권한이 없습니다.");

	}

	@Test
	void 오너라서_캘린더를_조회할_권한이_있다() {

		// given
		when(calendarDateRepository.existsByIdAndUser(calendarDate.getId(), owner)).thenReturn(true);

		// when & then
		assertThatCode(() -> permissionService.checkCalendarDateFetchPermission(calendarDate, owner))
			.doesNotThrowAnyException();

	}

	@Test
	void 서로_팔로잉_중이라서_캘린더를_조회할_권한이_있다() {

		// given
		when(followRepository.existsByToUserAndFromUser(owner, loggedInUser)).thenReturn(true);
		when(followRepository.existsByToUserAndFromUser(loggedInUser, owner)).thenReturn(true);

		// when & then
		assertThatCode(() -> permissionService.checkCalendarDateFetchPermission(calendarDate, loggedInUser))
			.doesNotThrowAnyException();

	}

	@Test
	void 캘린더를_조회할_권한이_없다() {

		// when & then
		assertThatThrownBy(() -> permissionService.checkCalendarDateFetchPermission(calendarDate, loggedInUser))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("해당 캘린더를 조회할 권한이 없습니다.");

	}

	@Test
	void 오너라서_먼슬리_캘린더를_조회할_권한이_있다() {

		// when & then
		assertThatCode(() -> permissionService.checkMonthlyFetchPermission(owner, owner))
			.doesNotThrowAnyException();

	}

	@Test
	void 서로_팔로잉_중이라서_먼슬리_캘린더를_조회할_권한이_있다() {

		// given
		when(followRepository.existsByToUserAndFromUser(owner, loggedInUser)).thenReturn(true);
		when(followRepository.existsByToUserAndFromUser(loggedInUser, owner)).thenReturn(true);

		// when & then
		assertThatCode(() -> permissionService.checkMonthlyFetchPermission(owner, loggedInUser))
			.doesNotThrowAnyException();

	}

	@Test
	void 먼슬리_캘린더를_조회할_권한이_없다() {

		// when & then
		assertThatThrownBy(() -> permissionService.checkMonthlyFetchPermission(owner, loggedInUser))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("해당 먼슬리 캘린더를 조회할 권한이 없습니다.");

	}

}