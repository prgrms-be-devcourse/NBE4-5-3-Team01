package com.team01.project.domain.notification.dto

import com.team01.project.domain.notification.entity.Notification
import java.time.LocalTime

data class NotificationDto(
    val id: Long?,
    val userId: String,
    val title: String,
    val message: String,
    val notificationTime: LocalTime,
    val isEmailEnabled: Boolean,
    val isPushEnabled: Boolean
) {
    constructor(notification: Notification) : this(
        id = notification.id,
        userId = notification.user.id,
        title = notification.title,
        message = notification.message,
        notificationTime = notification.notificationTime,
        isEmailEnabled = notification.isEmailEnabled,
        isPushEnabled = notification.isPushEnabled
    )
}
