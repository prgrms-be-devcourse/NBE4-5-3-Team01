package com.team01.project.global.permission;

import static com.team01.project.domain.follow.entity.type.Status.*;
import static com.team01.project.domain.user.entity.CalendarVisibility.*;
import static com.team01.project.global.permission.CalendarPermission.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.follow.entity.type.Status;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

	private final FollowRepository followRepository;

	/**
	 * 캘린더 권한 확인
	 * @param calendarOwner 캘린더 소유자
	 * @param loggedInUser 현재 인증된 유저
	 * @return 캘린더 권한
	 */
	public CalendarPermission checkPermission(User calendarOwner, User loggedInUser) {
		if (hasEditPermission(calendarOwner, loggedInUser)) {
			return EDIT;
		}

		if (hasViewPermission(calendarOwner, loggedInUser)) {
			return VIEW;
		}

		return CalendarPermission.NONE;
	}

	/**
	 * 캘린더 조회 가능 여부 확인
	 * @param calendarOwner 캘린더 소유자
	 * @param loggedInUser 현재 인증된 유저
	 * @return 캘린더 조회 가능 여부
	 */
	private boolean hasViewPermission(User calendarOwner, User loggedInUser) {

		// 캘린더가 전체 공개로 설정돼있는 경우 조회 가능
		if (isPublicCalendar(calendarOwner)) {
			return true;
		}

		// 캘린더가 팔로워 공개로 설정돼있는 경우 현재 인증된 유저가 캘린더 소유자의 팔로워라면 조회 가능
		if (isFollowerOnlyCalendar(calendarOwner) && isFollower(loggedInUser, calendarOwner)) {
			return true;
		}

		// 현재 인증된 유저가 캘린더 소유자인 경우 조회 가능
		if (isCalendarOwner(calendarOwner, loggedInUser)) {
			return true;
		}

		// 이외의 경우에는 조회 불가능
		return false;

	}

	/**
	 * 캘린더 수정 가능 여부 확인
	 * @param calendarOwner 캘린더 소유자
	 * @param loggedInUser 현재 인증된 유저
	 * @return 캘린더 수정 가능 여부
	 */
	private boolean hasEditPermission(User calendarOwner, User loggedInUser) {
		return isCalendarOwner(calendarOwner, loggedInUser);
	}

	private boolean isPublicCalendar(User calendarOwner) {
		return PUBLIC == calendarOwner.getCalendarVisibility();
	}

	private boolean isFollowerOnlyCalendar(User calendarOwner) {
		return FOLLOWER_ONLY == calendarOwner.getCalendarVisibility();
	}

	private boolean isFollower(User fromUser, User toUser) {
		return ACCEPT == followRepository.findStatusByToUserAndFromUser(toUser, fromUser).orElse(Status.NONE);

	}

	private boolean isCalendarOwner(User calendarOwner, User loggedInUser) {
		return calendarOwner.equals(loggedInUser);
	}

}