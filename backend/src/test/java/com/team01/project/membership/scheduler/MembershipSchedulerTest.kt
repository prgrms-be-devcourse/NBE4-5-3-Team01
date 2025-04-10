package com.team01.project.domain.user.scheduler

import com.team01.project.domain.user.entity.Membership
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDate

class MembershipSchedulerTest {

    private lateinit var userRepository: UserRepository
    private lateinit var membershipScheduler: MembershipScheduler

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        membershipScheduler = MembershipScheduler(userRepository)
    }

    private fun createUserWithMembership(
        id: String,
        grade: String,
        autoRenew: Boolean,
        endDate: LocalDate?
    ): User {
        val user = User(
            id = id,
            email = "$id@example.com",
            name = "테스트",
            calendarVisibility = com.team01.project.domain.user.entity.CalendarVisibility.PUBLIC
        )

        val membership = Membership(
            grade = grade,
            autoRenew = autoRenew,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = endDate,
            user = user
        )

        user.membership = membership
        return user
    }

    @Test
    fun `만료된 프리미엄 요금제는 기본 요금제로 변경된다`() {
        // given
        val today = LocalDate.now()
        val expiredPremium = createUserWithMembership("expired", "premium", false, today.minusDays(1))
        val activePremium = createUserWithMembership("active", "premium", true, today.plusDays(1))
        val basicUser = createUserWithMembership("basic", "basic", false, null)

        `when`(userRepository.findAll()).thenReturn(listOf(expiredPremium, activePremium, basicUser))

        // when
        membershipScheduler.downgradeExpiredMemberships()

        // then
        assertThat(expiredPremium.membership!!.grade).isEqualTo("basic")
        assertThat(expiredPremium.membership!!.endDate).isNull()
        assertThat(activePremium.membership!!.grade).isEqualTo("premium") // 유지
        assertThat(basicUser.membership!!.grade).isEqualTo("basic")       // 유지
    }
}
