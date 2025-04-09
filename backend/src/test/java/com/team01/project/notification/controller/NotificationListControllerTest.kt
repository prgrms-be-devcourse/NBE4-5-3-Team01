package com.team01.project.notification.controller

import com.team01.project.domain.notification.controller.NotificationListController
import com.team01.project.domain.notification.entity.NotificationList
import com.team01.project.domain.notification.service.NotificationListService
import com.team01.project.domain.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

@WithMockUser(username = "user123")
class NotificationListControllerTest {

    private lateinit var mockMvc: MockMvc
    private val notificationListService: NotificationListService = mock(NotificationListService::class.java)

    @BeforeEach
    fun setUp() {
        val controller = NotificationListController(notificationListService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(
                AuthenticationPrincipalArgumentResolver()
            )
            .build()

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
    fun `사용자 알림리스트 조회`() {
        val user = User(id = "user123", email = "test@test.com", name = "Tester")
        val notificationList = NotificationList(
            id = 1L,
            user = user,
            title = "알림제목",
            message = "알림 메시지",
            notificationTime = LocalDateTime.now(),
            isRead = false
        )

        given(notificationListService.getUserNotificationLists("user123"))
            .willReturn(listOf(notificationList))

        mockMvc.get("/notification-lists")
            .andExpect {
                status { isOk() }
                jsonPath("$.code") { value("200-1") }
                jsonPath("$.data[0].message") { value("알림 메시지") }
            }
    }

    @Test
    fun `알림 읽음 처리`() {
        mockMvc.patch("/notification-lists/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.msg") { value("Notification marked as read") }
            }

        then(notificationListService).should().markAsRead(1L, "user123")
    }

    @Test
    fun `모든 알림 읽음 처리`() {
        mockMvc.patch("/notification-lists/mark-all-read")
            .andExpect {
                status { isOk() }
                jsonPath("$.msg") { value("Notification marked All as read") }
            }

        then(notificationListService).should().markAllAsRead("user123")
    }

    @Test
    fun `알림 삭제`() {
        mockMvc.delete("/notification-lists/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.msg") { value("Notification deleted") }
            }

        then(notificationListService).should().deleteNotification(1L, "user123")
    }
}
