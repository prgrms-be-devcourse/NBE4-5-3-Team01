package com.team01.project.global.permission

import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.follow.repository.FollowRepository
import com.team01.project.domain.user.entity.CalendarVisibility
import com.team01.project.domain.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PermissionService(
    private val followRepository: FollowRepository
) {
    /**
     * 캘린더 권한 확인
     * @param calendarOwner 캘린더 소유자
     * @param loggedInUser 현재 인증된 유저
     * @return 캘린더 권한
     */
    fun checkPermission(calendarOwner: User, loggedInUser: User): CalendarPermission {
        return when {
            hasEditPermission(calendarOwner, loggedInUser) -> CalendarPermission.EDIT
            hasViewPermission(calendarOwner, loggedInUser) -> CalendarPermission.VIEW
            else -> CalendarPermission.NONE
        }
    }

    /**
     * 캘린더 조회 가능 여부 확인
     * @param calendarOwner 캘린더 소유자
     * @param loggedInUser 현재 인증된 유저
     * @return 캘린더 조회 가능 여부
     */
    private fun hasViewPermission(calendarOwner: User, loggedInUser: User): Boolean {
        // 조회 가능한 경우
        // [1] 전체 공개인 경우
        // [2] 팔로워 공개인 경우 현재 인증된 유저가 캘린더 소유자의 팔로워라면 조회 가능
        // [3] 비공개인 경우 현재 인증된 유저가 캘린더 소유자라면 조회 가능
        return isPublicCalendar(calendarOwner) ||
                (isFollowerOnlyCalendar(calendarOwner) && isFollower(loggedInUser, calendarOwner)) ||
                isCalendarOwner(calendarOwner, loggedInUser)
    }

    /**
     * 캘린더 수정 가능 여부 확인
     * @param calendarOwner 캘린더 소유자
     * @param loggedInUser 현재 인증된 유저
     * @return 캘린더 수정 가능 여부
     */
    private fun hasEditPermission(calendarOwner: User, loggedInUser: User): Boolean {
        return isCalendarOwner(calendarOwner, loggedInUser)
    }

    private fun isPublicCalendar(calendarOwner: User): Boolean {
        return CalendarVisibility.PUBLIC == calendarOwner.calendarVisibility
    }

    private fun isFollowerOnlyCalendar(calendarOwner: User): Boolean {
        return CalendarVisibility.FOLLOWER_ONLY == calendarOwner.calendarVisibility
    }

    private fun isFollower(fromUser: User, toUser: User): Boolean {
        return Status.ACCEPT == followRepository.findStatusByToUserAndFromUser(toUser, fromUser).orElse(Status.NONE)
    }

    private fun isCalendarOwner(calendarOwner: User, loggedInUser: User): Boolean {
        return calendarOwner == loggedInUser // ==: .equals() 호출 -> 내용 비교
                                             // ===: 참조 비교
    }
}
