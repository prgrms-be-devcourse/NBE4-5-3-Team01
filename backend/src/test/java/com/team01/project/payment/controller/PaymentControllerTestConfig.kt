package com.team01.project.payment.controller

import com.team01.project.domain.payment.service.TossService
import com.team01.project.domain.user.service.MembershipService
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class PaymentControllerTestConfig {
    @Bean
    fun tossService(): TossService = mock(TossService::class.java)

    @Bean
    fun membershipService(): MembershipService = mock(MembershipService::class.java)
}
