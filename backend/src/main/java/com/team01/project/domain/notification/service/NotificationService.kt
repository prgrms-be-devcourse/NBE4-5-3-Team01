package com.team01.project.domain.notification.service

import com.team01.project.domain.notification.constants.NotificationMessages
import com.team01.project.domain.notification.dto.NotificationUpdateDto
import com.team01.project.domain.notification.entity.Notification
import com.team01.project.domain.notification.event.NotificationInitEvent
import com.team01.project.domain.notification.repository.NotificationRepository
import com.team01.project.domain.notification.repository.SubscriptionRepository
import com.team01.project.domain.user.entity.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalTime

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val subscriptionRepository: SubscriptionRepository
) {

    fun getAllNotifications(): List<Notification> =
        notificationRepository.findAll()

    @Transactional(readOnly = true)
    fun getUserNotifications(userId: String): List<Notification> =
        notificationRepository.findByUserId(userId)

    @Transactional(readOnly = true)
    fun getNotification(notificationId: Long): Notification =
        notificationRepository.findById(notificationId)
            .orElseThrow { IllegalArgumentException("Notification not found with ID: $notificationId") }

    @Transactional(readOnly = true)
    fun getModifiableNotification(userId: String): List<Notification> {
        return notificationRepository.findByUserId(userId)
            .filter { it.title in listOf("DAILY CHALLENGE", "BUILD PLAYLIST", "YEAR HISTORY", "DAILY RECAP") }
    }

    @Transactional
    fun updateNotification(userId: String, notificationId: Long, notificationTime: LocalTime) {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow {
                IllegalArgumentException("Notification not found with ID: $notificationId")
            }

        if (notification.user.id != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this notification.")
        }

        notification.updateNotificationTime(notificationTime)
        notificationRepository.save(notification)

        // 이벤트 발행 주석처리된 부분 필요시 복원 가능
        // if (notification.notificationTime?.isBefore(LocalTime.now().plusMinutes(30)) == true) {
        //     eventPublisher.publishEvent(NotificationUpdatedEvent(this, notification.notificationTime))
        // }
    }

    @Transactional(readOnly = true)
    fun getNotificationsByTime(time: LocalTime): List<Notification> =
        notificationRepository.findByNotificationTime(time)

    @Transactional(readOnly = true)
    fun getNotificationTimeBetween(start: LocalTime, end: LocalTime): List<LocalTime> =
        notificationRepository.findDistinctNotificationTimeBetween(start, end)

    @Transactional
    fun createDefaultNotifications(user: User) {
        val notifications = NotificationMessages.DEFAULT_MESSAGES.map { (title, messageTemplate) ->
            val time = when (title) {
                "DAILY CHALLENGE" -> LocalTime.of(21, 0)
                "YEAR HISTORY" -> LocalTime.of(9, 0)
                "BUILD PLAYLIST" -> LocalTime.of(18, 0)
                "DAILY RECAP" -> LocalTime.of(23, 50)
                else -> null
            }

            Notification.builder()
                .user(user)
                .notificationTime(time)
                .title(title)
                .message(String.format(messageTemplate, user.name))
                .build()
        }

        notificationRepository.saveAll(notifications)
    }

    @Transactional
    fun updateNotifications(notifications: List<NotificationUpdateDto>, userId: String) {
        for (dto in notifications) {
            val notification = notificationRepository.findById(dto.notificationId)
                .orElseThrow {
                    ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found with ID: ${dto.notificationId}")
                }

            if (notification.user.id != userId) {
                throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this notification.")
            }

            notification.updateNotificationSettings(
                dto.isEmailNotificationEnabled,
                dto.isPushNotificationEnabled
            )
        }
    }

    @Transactional
    fun initLoginNotifications(time: LocalTime, user: User) {
        eventPublisher.publishEvent(NotificationInitEvent(this, time, user))
    }

    @Transactional
    fun deleteSubscription(userId: String) {
        subscriptionRepository.findByUserId(userId)
            .ifPresent { subscriptionRepository.delete(it) }
    }
}
