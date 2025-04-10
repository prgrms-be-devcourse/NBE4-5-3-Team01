package com.team01.project.membership.controller

import MembershipDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.team01.project.domain.user.controller.MembershipController
import com.team01.project.domain.user.service.MembershipService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.security.Principal
import java.time.LocalDate

@WebMvcTest(MembershipController::class)
@ContextConfiguration(classes = [MembershipController::class, MembershipControllerTest.TestConfig::class])
@Import(MembershipController::class)
class MembershipControllerTest {

    @Configuration
    class TestConfig {
        @Bean
        fun membershipService(): MembershipService = Mockito.mock(MembershipService::class.java)
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var membershipService: MembershipService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private fun principal(): Principal = Principal { "user123" }

    @BeforeEach
    fun setupSecurityContext() {
        val attributes = mapOf("id" to "user123")
        val user: OAuth2User = DefaultOAuth2User(
            listOf(), attributes, "id"
        )
        val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities)
        SecurityContextHolder.getContext().authentication = auth
    }

    @Test
    @WithMockUser
    fun `현재 사용자 멤버십 조회`() {
        val dto = MembershipDto(
            grade = "premium",
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            autoRenew = true
        )

        Mockito.`when`(membershipService.getCurrentUserMembership("user123")).thenReturn(dto)

        mockMvc.perform(get("/membership/my").principal(principal()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("200-1"))
            .andExpect(jsonPath("$.data.grade").value("premium"))
    }

    @Test
    @WithMockUser
    fun `요금제 해지`() {
        Mockito.doNothing().`when`(membershipService).cancelMembership("user123")

        mockMvc.perform(post("/membership/cancel").with(csrf()).principal(principal()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("200-2"))
    }

    @Test
    @WithMockUser
    fun `기본 멤버십 생성`() {
        Mockito.`when`(membershipService.initMembership("user123")).thenReturn(true)

        mockMvc.perform(post("/membership/init").with(csrf()).principal(principal()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("200-4"))
    }

    @Test
    @WithMockUser
    fun `전체 멤버십 조회`() {
        val memberships = listOf(mapOf("id" to "user123", "grade" to "basic"))
        Mockito.`when`(membershipService.getAllMemberships()).thenReturn(memberships)

        mockMvc.perform(get("/membership/admin").principal(principal()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("200-6"))
            .andExpect(jsonPath("$.data[0].grade").value("basic"))
    }

    @Test
    @WithMockUser
    fun `멤버십 정보 수정`() {
        val dto = MembershipDto(
            grade = "premium",
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            autoRenew = true
        )

        Mockito.doNothing().`when`(membershipService).updateMembership("user123", dto)

        mockMvc.perform(
            patch("/membership/admin/user123")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("200-7"))
    }
}
