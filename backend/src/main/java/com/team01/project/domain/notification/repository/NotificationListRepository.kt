package com.team01.project.domain.notification.repository

import com.team01.project.domain.notification.entity.NotificationList
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationListRepository : JpaRepository<NotificationList, Long> {
    fun findByUserId(userId: String): List<NotificationList>

    fun findByUserIdAndIsReadFalse(userId: String): List<NotificationList>
}
