package com.team01.project.permission.service

import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.follow.repository.FollowRepository
import com.team01.project.domain.user.entity.CalendarVisibility
import com.team01.project.domain.user.entity.User
import com.team01.project.global.permission.CalendarPermission
import com.team01.project.global.permission.PermissionService
import com.team01.project.util.TestDisplayNameGenerator
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.Test
import java.util.Optional

@DisplayNameGeneration(TestDisplayNameGenerator::class)
class PermissionServiceTest {
    private val followRepository: FollowRepository = mockk()
    private val permissionService = PermissionService(followRepository)

    private val calendarOwner = User(
        id = "calendar owner",
        email = "owner email",
        name = "owner name",
        calendarVisibility = CalendarVisibility.FOLLOWER_ONLY // 팔로워에게 공개
    )

    private val loggedInUser = User(
        id = "logged in user",
        email = "user email",
        name = "user name"
    )

    @Test
    fun `오너라서 캘린더를 수정할 권한이 있다`() {
        // when
        val calendarPermission = permissionService.checkPermission(calendarOwner, calendarOwner)

        // then
        assertEquals(CalendarPermission.EDIT, calendarPermission)
    }

    @Test
    fun `오너가 아니라서 캘린더를 수정할 권한이 없다`() {
        // given
        every { followRepository.findStatusByToUserAndFromUser(calendarOwner, loggedInUser) } returns Optional.of(Status.ACCEPT)

        // when
        val calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser)

        // then
        assertNotEquals(CalendarPermission.EDIT, calendarPermission)
    }

    @Test
    fun `오너라서 캘린더를 조회할 권한이 있다`() {
        // when
        val calendarPermission = permissionService.checkPermission(calendarOwner, calendarOwner)

        // then
        assertNotEquals(CalendarPermission.NONE, calendarPermission)
    }

    @Test
    fun `팔로워라서 팔로워 공개 캘린더를 조회할 권한이 있다`() {
        // given
        every { followRepository.findStatusByToUserAndFromUser(calendarOwner, loggedInUser) } returns Optional.of(Status.ACCEPT)

        // when
        val calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser)

        // then
        assertEquals(CalendarPermission.VIEW, calendarPermission)
    }

    @Test
    fun `캘린더를 조회할 권한이 없다`() {
        // given
        every { followRepository.findStatusByToUserAndFromUser(calendarOwner, loggedInUser) } returns Optional.of(Status.NONE)

        // when
        val calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser)

        // then
        assertEquals(CalendarPermission.NONE, calendarPermission)
    }
}