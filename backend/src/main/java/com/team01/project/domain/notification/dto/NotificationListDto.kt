package com.team01.project.domain.notification.dto

import com.team01.project.domain.notification.entity.NotificationList
import java.time.LocalDateTime

data class NotificationListDto(
    val id: Long?,
    val userId: String,
    val message: String,
    val notificationTime: LocalDateTime,
    val isRead: Boolean
) {
    constructor(notificationList: NotificationList) : this(
        id = notificationList.id,
        userId = notificationList.user.id,
        message = notificationList.message,
        notificationTime = notificationList.notificationTime,
        isRead = notificationList.isRead
    )
}
