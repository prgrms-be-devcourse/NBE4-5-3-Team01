package com.team01.project.notification.service

import com.team01.project.domain.notification.entity.NotificationList
import com.team01.project.domain.notification.repository.NotificationListRepository
import com.team01.project.domain.notification.service.NotificationListService
import com.team01.project.domain.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.ArgumentCaptor
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationListServiceTest {

    @Mock
    private lateinit var notificationListRepository: NotificationListRepository

    @InjectMocks
    private lateinit var notificationListService: NotificationListService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getUserNotificationLists - 유저의 알림 목록을 반환`() {
        // given
        val userId = "user123"
        val user = User(id = userId, email = "a@test.com", name = "Test")
        val notifications = listOf(
            NotificationList.builder().user(user).title("알림1").message("내용1").notificationTime(LocalDateTime.now()).build(),
            NotificationList.builder().user(user).title("알림2").message("내용2").notificationTime(LocalDateTime.now()).build()
        )
        given(notificationListRepository.findByUserId(userId)).willReturn(notifications)

        // when
        val result = notificationListService.getUserNotificationLists(userId)

        // then
        assertEquals(2, result.size)
    }

    @Test
    fun `markAsRead - 정상적으로 읽음 표시`() {
        // given
        val user = User(id = "user123", email = "a@test.com", name = "Test")
        val notification = NotificationList.builder()
            .id(1L)
            .user(user)
            .title("제목")
            .message("내용")
            .notificationTime(LocalDateTime.now())
            .isRead(false)
            .build()

        given(notificationListRepository.findById(1L)).willReturn(Optional.of(notification))

        // when
        notificationListService.markAsRead(1L, user.id)

        // then
        assertTrue(notification.isRead)
        then(notificationListRepository).should().save(notification)
    }

    @Test
    fun `markAsRead - 다른 유저의 알림이면 예외 발생`() {
        val user = User(id = "user123", email = "a@test.com", name = "Test")
        val otherUser = User(id = "other", email = "b@test.com", name = "Other")
        val notification = NotificationList.builder()
            .id(1L)
            .user(otherUser)
            .title("제목")
            .message("내용")
            .notificationTime(LocalDateTime.now())
            .isRead(false)
            .build()

        given(notificationListRepository.findById(1L)).willReturn(Optional.of(notification))

        // when & then
        val ex = assertThrows<ResponseStatusException> {
            notificationListService.markAsRead(1L, user.id)
        }

        assertEquals("403 FORBIDDEN \"You do not have permission to update this notification.\"", ex.message)
    }

    @Test
    fun `markAllAsRead - 안 읽은 알림 전체 읽음 처리`() {
        // given
        val userId = "user123"
        val user = User(id = userId, email = "a@test.com", name = "Test")

        val notifications = listOf(
            NotificationList.builder().user(user).isRead(false).title("알림1").message("msg").notificationTime(LocalDateTime.now()).build(),
            NotificationList.builder().user(user).isRead(false).title("알림2").message("msg").notificationTime(LocalDateTime.now()).build()
        )

        given(notificationListRepository.findByUserIdAndIsReadFalse(userId)).willReturn(notifications)

        // when
        notificationListService.markAllAsRead(userId)

        // then
        assertTrue(notifications.all { it.isRead })
        then(notificationListRepository).should().saveAll(notifications)
    }

    @Test
    fun `markAllAsRead - 읽지 않은 알림이 없으면 아무 것도 하지 않음`() {
        // given
        val userId = "user123"
        given(notificationListRepository.findByUserIdAndIsReadFalse(userId)).willReturn(emptyList())

        // when
        notificationListService.markAllAsRead(userId)

        // then
        then(notificationListRepository).should(never()).saveAll(anyList())
    }

    @Test
    fun `deleteNotification - 정상 삭제`() {
        // given
        val user = User(id = "user123", email = "test@test.com", name = "Test")
        val notification = NotificationList.builder()
            .id(1L)
            .user(user)
            .title("제목")
            .message("내용")
            .notificationTime(LocalDateTime.now())
            .build()

        given(notificationListRepository.findById(1L)).willReturn(Optional.of(notification))

        // when
        notificationListService.deleteNotification(1L, user.id)

        // then
        then(notificationListRepository).should().deleteById(1L)
    }

    @Test
    fun `deleteNotification - 다른 유저의 알림이면 예외`() {
        val user = User(id = "user123", email = "test@test.com", name = "Test")
        val otherUser = User(id = "otherUser", email = "other@test.com", name = "Other")
        val notification = NotificationList.builder()
            .id(1L)
            .user(otherUser)
            .title("제목")
            .message("내용")
            .notificationTime(LocalDateTime.now())
            .build()

        given(notificationListRepository.findById(1L)).willReturn(Optional.of(notification))

        val ex = assertThrows<ResponseStatusException> {
            notificationListService.deleteNotification(1L, user.id)
        }

        assertEquals("403 FORBIDDEN \"You do not have permission to delete this notification.\"", ex.message)
    }

    @Test
    fun `addNotification - 알림 저장 성공`() {
        // given
        val user = User(id = "user123", email = "test@test.com", name = "Test")
        val now = LocalDateTime.now()

        val captor = ArgumentCaptor.forClass(NotificationList::class.java)

        // when
        notificationListService.addNotification(user, "제목", "내용", now)

        // then
        verify(notificationListRepository).save(captor.capture())
        val saved = captor.value

        assertEquals("제목", saved.title)
        assertEquals("내용", saved.message)
        assertEquals(user, saved.user)
        assertEquals(now, saved.notificationTime)
    }
}
