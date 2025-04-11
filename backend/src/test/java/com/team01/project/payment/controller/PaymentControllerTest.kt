package com.team01.project.payment.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.team01.project.domain.payment.dto.TossPaymentRequest
import com.team01.project.domain.payment.dto.TossPaymentResponse
import com.team01.project.domain.payment.dto.TossSubscriptionRequest
import com.team01.project.domain.payment.dto.TossSubscriptionResponse
import com.team01.project.domain.payment.service.TossService
import com.team01.project.domain.user.service.MembershipService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@Import(PaymentControllerTestConfig::class)
class PaymentControllerTest {

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var tossService: TossService

    @Autowired
    private lateinit var membershipService: MembershipService

    @BeforeEach
    fun setupMockMvcAndSecurity() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()

        val attributes = mapOf("id" to "user123")
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val user: OAuth2User = DefaultOAuth2User(authorities, attributes, "id")
        val authentication = UsernamePasswordAuthenticationToken(user, null, authorities)

        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    fun `1회 결제 성공 시 멤버십 저장 및 응답 반환`() {
        val request = TossPaymentRequest("paymentKey123", "order123", 10000)
        val response = TossPaymentResponse("order123", "카드", "2025-04-10T13:00:00")

        `when`(tossService.confirmPayment(request)).thenReturn(response)
        doNothing().`when`(membershipService).saveOneTimePurchase("user123", "order123")

        mockMvc.perform(
            post("/payment/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("200"))
            .andExpect(jsonPath("$.msg").value("결제 및 멤버십 등록 성공"))
    }

    @Test
    fun `1회 결제 실패 시 오류 응답`() {
        val request = TossPaymentRequest("invalid", "failOrder", 5000)
        `when`(tossService.confirmPayment(request)).thenReturn(null)

        mockMvc.perform(
            post("/payment/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.code").value("500"))
            .andExpect(jsonPath("$.msg").value("결제 승인 실패"))
    }

    @Test
    fun `정기 결제 성공 시 프리미엄 업그레이드`() {
        val request = TossSubscriptionRequest("auth123", "user123")
        val response = TossSubscriptionResponse("billing123", "user123")

        `when`(tossService.processSubscription(request)).thenReturn(response)
        `when`(membershipService.upgradeMembership("user123", "billing123")).thenReturn(true)

        mockMvc.perform(
            post("/payment/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("200"))
            .andExpect(jsonPath("$.msg").value("결제 및 프리미엄 업그레이드 성공"))
    }

    @Test
    fun `정기 결제 billingKey 발급 실패 시 오류 응답`() {
        val request = TossSubscriptionRequest("authFail", "user123")
        `when`(tossService.processSubscription(request)).thenReturn(null)

        mockMvc.perform(
            post("/payment/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.code").value("500"))
            .andExpect(jsonPath("$.msg").value("billingKey 발급 실패"))
    }
}
