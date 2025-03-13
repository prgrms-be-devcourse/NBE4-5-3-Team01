package com.team01.project.global.permission;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.calendardate.repository.CalendarDateRepository;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

	private final CalendarDateRepository calendarDateRepository;
	private final FollowRepository followRepository;

	public void checkCalendarDateUpdatePermission(Long calendarDateId, User loggedInUser) {
		if (!isOwner(calendarDateId, loggedInUser)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 캘린더를 수정할 권한이 없습니다.");
		}
	}

	public void checkCalendarDateFetchPermission(CalendarDate calendarDate, User loggedInUser) {
		if (isOwner(calendarDate.getId(), loggedInUser) || isMutualFollowing(calendarDate.getUser(), loggedInUser)) {
			return; // 권한 있음
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 캘린더를 조회할 권한이 없습니다.");
	}

	public void checkMonthlyFetchPermission(User owner, User loggedInUser) {
		if (owner.getId().equals(loggedInUser.getId())) {
			return;
		} else if (isMutualFollowing(owner, loggedInUser)) {
			return;
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 먼슬리 캘린더를 조회할 권한이 없습니다.");
	}

	private boolean isOwner(Long calendarDateId, User loggedInUser) {
		return calendarDateRepository.existsByIdAndUser(calendarDateId, loggedInUser);
	}

	private boolean isMutualFollowing(User user1, User user2) {
		return followRepository.existsByToUserAndFromUser(user1, user2)
			&& followRepository.existsByToUserAndFromUser(user2, user1);
	}

}