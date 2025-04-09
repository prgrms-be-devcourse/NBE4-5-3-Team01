package com.team01.project.domain.notification.service

import com.team01.project.domain.notification.dto.NotificationUpdateDto
import com.team01.project.domain.notification.entity.Notification
import com.team01.project.domain.notification.repository.NotificationRepository
import com.team01.project.domain.notification.repository.SubscriptionRepository
import com.team01.project.domain.user.entity.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.web.server.ResponseStatusException
import java.time.LocalTime
import java.util.*

class NotificationServiceTest {

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @Mock
    private lateinit var subscriptionRepository: SubscriptionRepository

    @InjectMocks
    private lateinit var notificationService: NotificationService

    init {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `updateNotification - 정상 케이스`() {
        // given
        val user = User(id = "user123", name = "TestUser", email = "test@test.com")
        val notification = Notification(
            id = 1L,
            user = user,
            title = "DAILY CHALLENGE",
            notificationTime = LocalTime.of(9, 0),
            message = "${user.name}님, 하루 한 곡 기록 도전해보세요! 📅"
        )

        given(notificationRepository.findById(1L)).willReturn(Optional.of(notification))

        // when
        val newTime = LocalTime.of(10, 30)
        notificationService.updateNotification("user123", 1L, newTime)

        // then
        assertEquals(newTime, notification.notificationTime)
        then(notificationRepository).should().save(notification)
    }

    @Test
    fun `updateNotification - 다른 유저가 수정 시도`() {
        // given
        val user = User(id = "user123", name = "TestUser", email = "test@test.com")
        val otherUser = User(id = "otherUser", name = "OtherUser", email = "other@test.com")

        val notification = Notification(
            id = 1L,
            user = otherUser,
            title = "DAILY CHALLENGE",
            notificationTime = LocalTime.of(9, 0),
            message = "Hello"
        )

        given(notificationRepository.findById(1L)).willReturn(Optional.of(notification))

        // when & then
        val exception = assertThrows<ResponseStatusException> {
            notificationService.updateNotification("user123", 1L, LocalTime.of(10, 0))
        }

        assertEquals("403 FORBIDDEN \"You do not have permission to update this notification.\"", exception.message)
    }

    @Test
    fun `getModifiableNotification - 특정 제목 필터링`() {
        // given
        val userId = "user123"
        val allNotifications = listOf(
            Notification(1L, User(userId, "User", "test@test.com", "user1"), "DAILY CHALLENGE", "msg", LocalTime.now()),
            Notification(2L, User(userId, "User", "test@test.com", "user1"), "BUILD PLAYLIST", "msg", LocalTime.now()),
            Notification(3L, User(userId, "User", "test@test.com", "user1"), "OTHER", "msg", LocalTime.now())
        )
        given(notificationRepository.findByUserId(userId)).willReturn(allNotifications)

        // when
        val result = notificationService.getModifiableNotification(userId)

        // then
        assertEquals(2, result.size)
        assertTrue(result.all { it.title != "OTHER" })
    }

    @Test
    fun `getUserNotifications - 유저의 모든 알림을 반환`() {
        // given
        val userId = "user123"
        val user = User(id = userId, email = "test@test.com", name = "TestUser")
        val notifications = listOf(
            Notification(user = user, title = "A", message = "msg"),
            Notification(user = user, title = "B", message = "msg2")
        )
        given(notificationRepository.findByUserId(userId)).willReturn(notifications)

        // when
        val result = notificationService.getUserNotifications(userId)

        // then
        assertEquals(2, result.size)
        assertEquals("A", result[0].title)
    }

    @Test
    fun `createDefaultNotifications - 기본 알림 생성 및 저장`() {
        // given
        val user = User(id = "user123", email = "user@test.com", name = "TestName")
        val capturedNotifications = mutableListOf<Notification>()

        willAnswer {
            capturedNotifications.addAll(it.getArgument<List<Notification>>(0))
            null
        }.given(notificationRepository).saveAll(anyList())

        // when
        notificationService.createDefaultNotifications(user)

        // then
        assertEquals(6, capturedNotifications.size)
        assertTrue(
            capturedNotifications.any {
                it.title == "DAILY CHALLENGE" && it.notificationTime == LocalTime.of(
                    21,
                    0
                )
            }
        )
        assertTrue(capturedNotifications.all { it.message.contains("TestName") })
    }

    @Test
    fun `updateNotifications - 사용자의 알림 설정을 성공적으로 수정`() {
        // given
        val user = User(id = "user123", email = "test@test.com", name = "TestUser")
        val notification = Notification(
            id = 1L,
            user = user,
            title = "TEST",
            message = "msg"
        )

        val dto = NotificationUpdateDto(
            notificationId = 1L,
            isEmailNotificationEnabled = false,
            isPushNotificationEnabled = true
        )

        given(notificationRepository.findById(1L)).willReturn(Optional.of(notification))

        // when
        notificationService.updateNotifications(listOf(dto), user.id)

        // then
        assertFalse(notification.isEmailEnabled)
        assertTrue(notification.isPushEnabled)
    }

    @Test
    fun `updateNotifications - 다른 사용자의 알림이면 예외 발생`() {
        // given
        val user = User(id = "user123", email = "test@test.com", name = "TestUser")
        val otherUser = User(id = "otherUser", email = "o@test.com", name = "Other")
        val notification = Notification(id = 1L, user = otherUser, title = "TEST", message = "msg")

        val dto = NotificationUpdateDto(
            notificationId = 1L,
            isEmailNotificationEnabled = false,
            isPushNotificationEnabled = false
        )

        given(notificationRepository.findById(1L)).willReturn(Optional.of(notification))

        // when & then
        val ex = assertThrows<ResponseStatusException> {
            notificationService.updateNotifications(listOf(dto), user.id)
        }

        assertEquals("403 FORBIDDEN \"You do not have permission to update this notification.\"", ex.message)
    }

    @Test
    fun `deleteSubscription - 해당 userId의 구독이 있으면 삭제됨`() {
        // given
        val userId = "user123"
        val mockSubscription = mock(com.team01.project.domain.notification.entity.Subscription::class.java)
        given(subscriptionRepository.findByUserId(userId)).willReturn(Optional.of(mockSubscription))

        // when
        notificationService.deleteSubscription(userId)

        // then
        then(subscriptionRepository).should().delete(mockSubscription)
    }
}
