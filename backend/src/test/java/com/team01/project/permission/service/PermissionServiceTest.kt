package com.team01.project.permission.service;

import static com.team01.project.domain.follow.entity.type.Status.*;
import static com.team01.project.global.permission.CalendarPermission.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team01.project.domain.calendardate.repository.CalendarDateRepository;
import com.team01.project.domain.follow.entity.type.Status;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.user.entity.CalendarVisibility;
import com.team01.project.domain.user.entity.User;
import com.team01.project.global.permission.CalendarPermission;
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

	private User calendarOwner;
	private User loggedInUser;

	@BeforeEach
	void 초기화() {
		calendarOwner = User.builder()
			.id("calendar owner")
			.calendarVisibility(CalendarVisibility.FOLLOWER_ONLY) // 팔로워에게 공개
			.build();
		loggedInUser = User.builder()
			.id("logged in user")
			.build();
	}

	@Test
	void 오너라서_캘린더를_수정할_권한이_있다() {

		// when
		CalendarPermission calendarPermission = permissionService.checkPermission(calendarOwner, calendarOwner);

		// then
		assertEquals(EDIT, calendarPermission);

	}

	@Test
	void 오너가_아니라서_캘린더를_수정할_권한이_없다() {

		// given
		when(followRepository.findStatusByToUserAndFromUser(any(User.class), any(User.class)))
			.thenReturn(Optional.of(ACCEPT));

		// when
		CalendarPermission calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser);

		// then
		assertNotEquals(EDIT, calendarPermission);

	}

	@Test
	void 오너라서_캘린더를_조회할_권한이_있다() {

		// when
		CalendarPermission calendarPermission = permissionService.checkPermission(calendarOwner, calendarOwner);

		// then
		assertNotEquals(CalendarPermission.NONE, calendarPermission);

	}

	@Test
	void 팔로워라서_팔로워_공개_캘린더를_조회할_권한이_있다() {

		// given
		when(followRepository.findStatusByToUserAndFromUser(any(User.class), any(User.class)))
			.thenReturn(Optional.of(ACCEPT));

		// when
		CalendarPermission calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser);

		// then
		assertEquals(VIEW, calendarPermission);

	}

	@Test
	void 캘린더를_조회할_권한이_없다() {

		// given
		when(followRepository.findStatusByToUserAndFromUser(any(User.class), any(User.class)))
			.thenReturn(Optional.of(Status.NONE));

		// when
		CalendarPermission calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser);

		// then
		assertEquals(CalendarPermission.NONE, calendarPermission);

	}

}