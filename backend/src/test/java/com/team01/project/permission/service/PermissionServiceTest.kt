package com.team01.project.permission.service

import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.follow.repository.FollowRepository
import com.team01.project.domain.user.entity.CalendarVisibility
import com.team01.project.global.permission.CalendarPermission
import com.team01.project.global.permission.PermissionService
import com.team01.project.user.entity.UserFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class PermissionServiceTest : BehaviorSpec({
    val followRepository = mockk<FollowRepository>()
    val permissionService = PermissionService(followRepository)

    val owner = UserFixture.유저("owner")
    val follower = UserFixture.유저("follower")
    val stranger = UserFixture.유저("stranger")

    beforeTest { clearAllMocks() }

    Given("캘린더 소유자가 캘린더를 전체 공개로 설정한 경우") {
        owner.calendarVisibility = CalendarVisibility.PUBLIC

        When("본인이 접근하면") {
            val permission = permissionService.checkPermission(owner, owner)

            Then("EDIT 권한을 가진다") {
                permission shouldBe CalendarPermission.EDIT
            }
        }

        When("팔로워가 접근하면") {
            val permission = permissionService.checkPermission(owner, follower)

            Then("VIEW 권한을 가진다") {
                permission shouldBe CalendarPermission.VIEW
            }
        }

        When("제3자가 접근하면") {
            val permission = permissionService.checkPermission(owner, stranger)

            Then("VIEW 권한을 가진다") {
                permission shouldBe CalendarPermission.VIEW
            }
        }
    }

    Given("캘린더 소유자가 캘린더를 팔로워 공개로 설정한 경우") {
        owner.calendarVisibility = CalendarVisibility.FOLLOWER_ONLY

        When("본인이 접근하면") {
            val permission = permissionService.checkPermission(owner, owner)

            Then("EDIT 권한을 가진다") {
                permission shouldBe CalendarPermission.EDIT
            }
        }

        When("팔로잉 요청이 수락된 팔로워가 접근하면") {
            beforeTest {
                every { followRepository.findStatusByToUserAndFromUser(eq(owner), eq(follower)) } returns Optional.of(Status.ACCEPT)
            }

            val permission by lazy {
                permissionService.checkPermission(owner, follower)
            }

            Then("VIEW 권한을 가진다") {
                permission shouldBe CalendarPermission.VIEW
            }
        }

        When("팔로잉 요청이 수락되지 않은 제3자가 접근하면") {
            beforeTest {
                every { followRepository.findStatusByToUserAndFromUser(owner, stranger) } returns Optional.of(Status.NONE)
            }

            val permission by lazy {
                permissionService.checkPermission(owner, stranger)
            }

            Then("어떠한 권한도 가지지 못한다") {
                permission shouldBe CalendarPermission.NONE
            }
        }
    }

    Given("캘린더 소유자가 캘린더를 비공개로 설정한 경우") {
        owner.calendarVisibility = CalendarVisibility.PRIVATE

        When("본인이 접근하면") {
            val permission = permissionService.checkPermission(owner, owner)

            Then("EDIT 권한을 가진다") {
                permission shouldBe CalendarPermission.EDIT
            }
        }

        When("팔로워가 접근하면") {
            val permission = permissionService.checkPermission(owner, follower)

            Then("어떠한 권한도 가지지 못한다") {
                permission shouldBe CalendarPermission.NONE
            }
        }

        When("제3자가 접근하면") {
            val permission = permissionService.checkPermission(owner, follower)

            Then("어떠한 권한도 가지지 못한다") {
                permission shouldBe CalendarPermission.NONE
            }
        }
    }
})
