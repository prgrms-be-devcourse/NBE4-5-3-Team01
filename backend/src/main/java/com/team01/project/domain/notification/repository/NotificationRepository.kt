package com.team01.project.domain.notification.repository

import com.team01.project.domain.notification.entity.Notification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalTime

interface NotificationRepository : JpaRepository<Notification, Long> {

    fun findByUserId(userId: String): List<Notification>

    @EntityGraph(attributePaths = ["user"])
    fun findByNotificationTime(notificationTime: LocalTime): List<Notification>

    @Query(
        "SELECT n FROM Notification n WHERE n.notificationTime >= :now AND n.notificationTime < :plusMinutes"
    )
    fun findNotificationsBetween(
        @Param("now") now: LocalTime,
        @Param("plusMinutes") plusMinutes: LocalTime
    ): List<Notification>

    @Query(
        "SELECT DISTINCT n.notificationTime FROM Notification n " +
                "WHERE n.notificationTime >= :start AND n.notificationTime < :end ORDER BY n.notificationTime ASC"
    )
    fun findDistinctNotificationTimeBetween(
        @Param("start") start: LocalTime,
        @Param("end") end: LocalTime
    ): List<LocalTime>
}
