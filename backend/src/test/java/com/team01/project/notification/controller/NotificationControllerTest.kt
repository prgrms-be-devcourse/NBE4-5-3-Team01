package com.team01.project.notification.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.team01.project.domain.notification.controller.NotificationController
import com.team01.project.domain.notification.dto.ModifyNotificationReqBody
import com.team01.project.domain.notification.dto.NotificationUpdateDto
import com.team01.project.domain.notification.entity.Notification
import com.team01.project.domain.notification.service.NotificationService
import com.team01.project.domain.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.mockito.Mockito.mock
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalTime

@WithMockUser(username = "user123")
class NotificationControllerTest {

    private lateinit var mockMvc: MockMvc
    private val notificationService: NotificationService = mock(NotificationService::class.java)
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        val controller = NotificationController(notificationService)

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(
                AuthenticationPrincipalArgumentResolver()
            )
            .build()

        objectMapper.registerModule(JavaTimeModule())

        val oauthUser = mock(OAuth2User::class.java).apply {
            `when`(name).thenReturn("user123")
        }

        val auth = UsernamePasswordAuthenticationToken(
            oauthUser,
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )

        SecurityContextHolder.getContext().authentication = auth
    }

    @Test
    fun `사용자 알림 목록 조회`() {
        // given
        val mockUser = User(id = "user123", email = "test@test.com", name = "TestUser")
        val mockNotification = Notification.builder()
            .user(mockUser)
            .title("DAILY CHALLENGE")
            .message("Hello")
            .notificationTime(LocalTime.of(9, 0))
            .build()

        given(notificationService.getUserNotifications("user123"))
            .willReturn(listOf(mockNotification))

        // when & then
        mockMvc.get("/notifications/lists")
            .andExpect {
                status { isOk() }
                jsonPath("$.code") { value("200-1") }
                jsonPath("$.data[0].title") { value("DAILY CHALLENGE") }
            }
    }

    @Test
    fun `알림 시간 수정`() {
        val body = ModifyNotificationReqBody(notificationTime = LocalTime.of(20, 0))

        mockMvc.put("/notifications/1/modify") {
            with(csrf())
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.andExpect {
            status { isOk() }
            jsonPath("$.msg") { value("Notification modified") }
        }

        // service 호출 확인
        then(notificationService).should().updateNotification("user123", 1L, body.notificationTime)
    }

    @Test
    fun `알림 설정 변경`() {
        val body = listOf(
            NotificationUpdateDto(
                notificationId = 1L,
                isEmailNotificationEnabled = false,
                isPushNotificationEnabled = false
            )
        )

        mockMvc.patch("/notifications/update") {
            with(csrf())
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.andExpect {
            status { isOk() }
            jsonPath("$.msg") { value("Notification updated") }
        }

        then(notificationService).should().updateNotifications(body, "user123")
    }
}
