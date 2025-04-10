package com.team01.project.membership.service

import MembershipDto
import com.team01.project.domain.user.entity.Membership
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.domain.user.service.MembershipService
import com.team01.project.global.exception.MembershipErrorCode
import com.team01.project.global.exception.MembershipException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.time.LocalDate
import java.util.*

class MembershipServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var membershipService: MembershipService

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        membershipService = MembershipService(userRepository)
    }

    private fun createUserWithMembership(
        grade: String = "basic",
        autoRenew: Boolean = false,
        count: Int = 0
    ): User {
        val user = User(
            id = "user123",
            email = "test@example.com",
            name = "홍길동",
            calendarVisibility = com.team01.project.domain.user.entity.CalendarVisibility.PUBLIC
        )

        val membership = Membership(
            grade = grade,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            autoRenew = autoRenew,
            count = count,
            user = user
        )
        user.membership = membership
        return user
    }

    @Test
    fun `현재 사용자 멤버십 정상 조회`() {
        val user = createUserWithMembership()
        `when`(userRepository.findById("user123")).thenReturn(Optional.of(user))

        val result = membershipService.getCurrentUserMembership("user123")

        assertThat(result.grade).isEqualTo("basic")
        assertThat(result.startDate).isEqualTo(LocalDate.of(2024, 1, 1))
        assertThat(result.autoRenew).isFalse()
    }

    @Test
    fun `멤버십 없는 경우 예외 발생`() {
        val user = createUserWithMembership()
        user.membership = null
        `when`(userRepository.findById("user123")).thenReturn(Optional.of(user))

        assertThatThrownBy {
            membershipService.getCurrentUserMembership("user123")
        }.isInstanceOf(MembershipException::class.java)
            .hasMessageContaining(MembershipErrorCode.MEMBERSHIP_NOT_FOUND.message)
    }

    @Test
    fun `프리미엄 요금제 해지 성공`() {
        val user = createUserWithMembership(grade = "premium", autoRenew = true)
        `when`(userRepository.findById("user123")).thenReturn(Optional.of(user))

        membershipService.cancelMembership("user123")

        assertThat(user.membership!!.autoRenew).isFalse()
        verify(userRepository).save(user)
    }

    @Test
    fun `프리미엄이 아닌 경우 해지 실패`() {
        val user = createUserWithMembership(grade = "basic")
        `when`(userRepository.findById("user123")).thenReturn(Optional.of(user))

        assertThatThrownBy {
            membershipService.cancelMembership("user123")
        }.isInstanceOf(MembershipException::class.java)
            .hasMessageContaining(MembershipErrorCode.NOT_PREMIUM.message)
    }

    @Test
    fun `기본 멤버십 신규 생성`() {
        val user = createUserWithMembership()
        user.membership = null
        `when`(userRepository.findById("user123")).thenReturn(Optional.of(user))

        val created = membershipService.initMembership("user123")

        assertThat(created).isTrue()
        assertThat(user.membership).isNotNull()
        assertThat(user.membership!!.grade).isEqualTo("basic")
        verify(userRepository).save(user)
    }

    @Test
    fun `기존 멤버십이 존재하면 생성되지 않음`() {
        val user = createUserWithMembership()
        `when`(userRepository.findById("user123")).thenReturn(Optional.of(user))

        val created = membershipService.initMembership("user123")

        assertThat(created).isFalse()
        verify(userRepository, never()).save(user)
    }

    @Test
    fun `멤버십 정보 수정 성공`() {
        val user = createUserWithMembership()
        `when`(userRepository.findById("user123")).thenReturn(Optional.of(user))

        val dto = MembershipDto("premium", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), true)

        membershipService.updateMembership("user123", dto)

        val updated = user.membership!!
        assertThat(updated.grade).isEqualTo("premium")
        assertThat(updated.autoRenew).isTrue()
        assertThat(updated.startDate).isEqualTo(dto.startDate)
        assertThat(updated.endDate).isEqualTo(dto.endDate)
        verify(userRepository).save(user)
    }

    @Test
    fun `전체 멤버십 목록 조회시 null 멤버십은 제외`() {
        val userWithMembership = createUserWithMembership()
        val userWithoutMembership = User(
            id = "user124",
            email = "test2@example.com",
            name = "김철수",
            calendarVisibility = com.team01.project.domain.user.entity.CalendarVisibility.PUBLIC
        )
        userWithoutMembership.membership = null

        `when`(userRepository.findAll()).thenReturn(listOf(userWithMembership, userWithoutMembership))

        val result = membershipService.getAllMemberships()

        assertThat(result).hasSize(1)
        assertThat(result[0]["userId"]).isEqualTo("user123")
        assertThat(result[0]["grade"]).isEqualTo("basic")
    }

    @Test
    fun `프리미엄 멤버십으로 업그레이드 성공`() {
        val user = createUserWithMembership(grade = "basic", autoRenew = false)
        `when`(userRepository.findById("user123")).thenReturn(Optional.of(user))

        val result = membershipService.upgradeMembership("user123", "billing-key-001")

        val upgraded = user.membership!!
        assertThat(result).isTrue()
        assertThat(upgraded.grade).isEqualTo("premium")
        assertThat(upgraded.autoRenew).isTrue()
        assertThat(upgraded.billingKey).isEqualTo("billing-key-001")
        assertThat(upgraded.count).isEqualTo(1)
        verify(userRepository).save(user)
    }

    @Test
    fun `이미 프리미엄이고 autoRenew가 true이면 업그레이드 실패`() {
        val user = createUserWithMembership(grade = "premium", autoRenew = true)
        `when`(userRepository.findById("user123")).thenReturn(Optional.of(user))

        assertThatThrownBy {
            membershipService.upgradeMembership("user123", "billing-key-001")
        }.isInstanceOf(MembershipException::class.java)
            .hasMessageContaining(MembershipErrorCode.ALREADY_PREMIUM.message)
    }

    @Test
    fun `1회성 프리미엄 결제 저장`() {
        val user = createUserWithMembership(grade = "basic", autoRenew = false, count = 0)
        `when`(userRepository.findById("user123")).thenReturn(Optional.of(user))

        membershipService.saveOneTimePurchase("user123", "order-001")

        val membership = user.membership!!
        assertThat(membership.grade).isEqualTo("premium")
        assertThat(membership.autoRenew).isFalse()
        assertThat(membership.count).isEqualTo(1)
        assertThat(membership.startDate).isEqualTo(LocalDate.now())
        assertThat(membership.endDate).isEqualTo(LocalDate.now().plusMonths(1))
        verify(userRepository).save(user)
    }
}
