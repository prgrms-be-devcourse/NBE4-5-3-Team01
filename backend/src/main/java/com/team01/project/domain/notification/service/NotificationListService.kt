package com.team01.project.domain.notification.service

import com.team01.project.domain.notification.entity.NotificationList
import com.team01.project.domain.notification.repository.NotificationListRepository
import com.team01.project.domain.user.entity.User
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class NotificationListService(
    private val notificationListRepository: NotificationListRepository
) {

    @Transactional(readOnly = true)
    fun getUserNotificationLists(userId: String): List<NotificationList> {
        return notificationListRepository.findByUserId(userId)
    }

    @Transactional
    fun markAsRead(notificationListId: Long, userId: String) {
        val notificationList = notificationListRepository.findById(notificationListId)
            .orElseThrow {
                IllegalArgumentException("Notification not found with ID: $notificationListId")
            }

        if (notificationList.user.id != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this notification.")
        }

        notificationList.markAsRead()
        notificationListRepository.save(notificationList)
    }

    @Transactional
    fun markAllAsRead(userId: String) {
        val notifications = notificationListRepository.findByUserIdAndIsReadFalse(userId)

        if (notifications.isEmpty()) return

        notifications.forEach { it.markAsRead() }
        notificationListRepository.saveAll(notifications)
    }

    @Transactional
    fun deleteNotification(notificationListId: Long, userId: String) {
        val notificationList = notificationListRepository.findById(notificationListId)
            .orElseThrow {
                IllegalArgumentException("Notification not found with ID: $notificationListId")
            }

        if (notificationList.user.id != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this notification.")
        }

        notificationListRepository.deleteById(notificationListId)
    }

    @Transactional
    fun addNotification(user: User, title: String, message: String, notificationTime: LocalDateTime) {
        val notification = NotificationList.builder()
            .user(user)
            .title(title)
            .message(message)
            .notificationTime(notificationTime)
            .build()

        notificationListRepository.save(notification)
    }
}
