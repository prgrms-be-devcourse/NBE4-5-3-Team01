package com.team01.project.payment.scheduler

import com.team01.project.domain.payment.scheduler.AutoBilling
import com.team01.project.domain.payment.service.TossService
import com.team01.project.domain.user.entity.Membership
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class AutoBillingTest {

    private lateinit var tossService: TossService
    private lateinit var userRepository: UserRepository
    private lateinit var autoBilling: AutoBilling

    private lateinit var membership: Membership
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        tossService = mock(TossService::class.java)
        userRepository = mock(UserRepository::class.java)
        autoBilling = AutoBilling(userRepository, tossService)

        user = User(
            id = "user123",
            name = "홍길동",
            email = "test@test.com"
        )

        membership = Membership(
            id = 1L,
            user = user,
            grade = "PREMIUM",
            startDate = LocalDate.of(2025, 3, 10),
            endDate = LocalDate.of(2025, 4, 10),
            count = 1,
            autoRenew = true,
            billingKey = "billing123",
            failCount = 0
        )

        // 사용자에 멤버십 연결
        user.membership = membership
    }

    @Test
    fun `자동 결제 성공 시 종료일 연장 및 실패횟수 초기화`() {
        `when`(userRepository.findAll()).thenReturn(listOf(user))
        `when`(tossService.chargeBillingKey(any(), any(), any(), any())).thenReturn(true)

        autoBilling.processAutoBilling()

        val expectedEndDate = LocalDate.now().plusMonths(1)
        assertEquals(expectedEndDate, membership.endDate)
        assertEquals(0, membership.failCount)
        assertTrue(membership.autoRenew)
    }

    @Test
    fun `자동 결제 실패 시 실패횟수 증가`() {
        membership.failCount = 0
        `when`(userRepository.findAll()).thenReturn(listOf(user))
        `when`(tossService.chargeBillingKey(any(), any(), any(), any())).thenReturn(false)

        autoBilling.processAutoBilling()

        assertEquals(1, membership.failCount)
        assertTrue(membership.autoRenew)
    }

    @Test
    fun `자동 결제 3회 실패 시 autoRenew false로 변경`() {
        membership.failCount = 2
        `when`(userRepository.findAll()).thenReturn(listOf(user))
        `when`(tossService.chargeBillingKey(any(), any(), any(), any())).thenReturn(false)

        autoBilling.processAutoBilling()

        assertEquals(3, membership.failCount)
        assertFalse(membership.autoRenew)
    }
}
