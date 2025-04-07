package com.team01.project.domain.notification.service

import com.team01.project.domain.notification.entity.Notification
import com.team01.project.domain.notification.event.*
import com.team01.project.domain.user.entity.User
import jakarta.annotation.PostConstruct
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.ScheduledFuture

@Service
class NotificationScheduler(
    private val notificationService: NotificationService,
    private val notificationSender: NotificationSender,
    private val taskScheduler: ThreadPoolTaskScheduler,
    private val separateTaskScheduler: ThreadPoolTaskScheduler
) {

    private val scheduledTasks: MutableList<CustomScheduledTask> = mutableListOf()

    @PostConstruct
    fun init() {
        println("애플리케이션 시작! 알림 스케줄링 호출")
        scheduleNotifications()
    }

    @Async
    @EventListener
    fun handleNotificationUpdated(event: NotificationUpdatedEvent) {
        println("🔔 알림 변경 감지됨! 스케줄링을 다시 설정합니다.")
        scheduleNotifications()
    }

    @Scheduled(cron = "0 29,59 * * * *")
    fun scheduleNotifications() {
        val now = LocalTime.now()
        val plusMinutes = now.plusMinutes(30)

        val notificationTimes = notificationService.getNotificationTimeBetween(now, plusMinutes)

        println("현재 시간 기준으로 다음 30분 동안 알림이 있는지 확인. 현재 시간 : $now 다음 체크 시간 : $plusMinutes")

        if (notificationTimes.isEmpty()) {
            println("다음 30분 내 알림 없음. 매 30분마다 체크.")
            return
        }

        cancelCompletedScheduledTasks()

        notificationTimes.forEach { scheduleNotificationSending(it) }
    }

    private fun cancelCompletedScheduledTasks() {
        val iterator = scheduledTasks.iterator()
        while (iterator.hasNext()) {
            val task = iterator.next()
            if (task.futureTask.isDone || LocalTime.now().isAfter(task.scheduledTime)) {
                iterator.remove()
            }
        }
    }

    private fun scheduleNotificationSending(notificationTime: LocalTime) {
        val notificationDateTime = LocalDateTime.now().withHour(notificationTime.hour)
            .withMinute(notificationTime.minute)
            .withSecond(0)
            .withNano(0)

        val scheduledTime = Date.from(notificationDateTime.atZone(ZoneId.systemDefault()).toInstant())

        val notifications = notificationService.getNotificationsByTime(notificationTime)
            .filter { it.isEmailEnabled || it.isPushEnabled }

        if (notifications.isEmpty()) {
            println("활성화된 알림이 없습니다. 알림 전송을 취소합니다.")
            return
        }

        val futureTask = taskScheduler.schedule({
            sendNotifications(notifications, notificationDateTime)
        }, scheduledTime)

        insertTaskInOrder(futureTask, notificationTime)
        println("알림 전송 예약 시각: $scheduledTime")
    }

    private fun insertTaskInOrder(futureTask: ScheduledFuture<*>, notificationTime: LocalTime) {
        val scheduledTask = CustomScheduledTask(futureTask, notificationTime)
        val index = scheduledTasks.indexOfFirst { it.scheduledTime.isAfter(notificationTime) }
        if (index >= 0) {
            scheduledTasks.add(index, scheduledTask)
        } else {
            scheduledTasks.add(scheduledTask)
        }
    }

    private fun sendNotification(notification: Notification, notificationTime: LocalDateTime) {
        if (notification.isPushEnabled) {
            notificationSender.sendPush(notification.user, notification.title, notification.message, notificationTime)
        }
        if (notification.isEmailEnabled) {
            notificationSender.sendEmail(notification.user, notification.title, notification.message)
        }
    }

    private fun sendNotifications(notifications: List<Notification>, notificationTime: LocalDateTime) {
        notifications.forEach {
            sendNotification(it, notificationTime)
        }
    }

    @Async
    @EventListener
    fun handleNotificationInit(event: NotificationInitEvent) {
        println("🔔 새로운 유저 로그인!")
        scheduleNotificationInitSending(event.time, event.user)
    }

    private fun scheduleNotificationInitSending(notificationTime: LocalTime, user: User) {
        scheduleSingleNotification(
            user,
            notificationTime.plusMinutes(2),
            "WELCOME",
            "${user.name}님, 환영합니다! 🎉"
        )
        scheduleSingleNotification(
            user,
            notificationTime.plusMinutes(5),
            "START RECORDING",
            "${user.name}님, 음악 기록을 시작해보세요! 🎵"
        )
    }

    private fun scheduleSingleNotification(user: User, notificationTime: LocalTime, title: String, message: String) {
        val notificationDateTime = LocalDateTime.now().withHour(notificationTime.hour)
            .withMinute(notificationTime.minute)
            .withSecond(0)
            .withNano(0)

        val scheduledTime = Date.from(notificationDateTime.atZone(ZoneId.systemDefault()).toInstant())

        val notification = Notification.builder()
            .user(user)
            .notificationTime(notificationTime)
            .title(title)
            .message(message)
            .build()

        val futureTask = separateTaskScheduler.schedule({
            sendNotification(notification, notificationDateTime)
        }, scheduledTime)

        insertTaskInOrder(futureTask, notificationTime)
        println("알림 전송 예약 시각: $scheduledTime")
    }

    @Async
    @EventListener
    fun handleNotificationAsync(event: NotificationFollowEvent) {
        println("🔔 새로운 팔로우 알림!")
        scheduleNotificationFollowSending(
            event.time,
            event.toUser,
            "FOLLOWING",
            "${event.fromUser.name}님이 회원님을 팔로우하기 시작했습니다."
        )
    }

    @Async
    @EventListener
    fun handleNotificationAsync(event: NotificationRecordEvent) {
        println("🔔 ${event.user.name}님의 새로운 음악 등록 알림!")
        scheduleNotificationFollowSending(
            event.time,
            event.user,
            "SHARE MUSIC",
            "${event.user.name}님, 오늘도 음악을 등록하셨네요! 회원님이 오늘 등록한 음악을 공유해보세요! 🎶"
        )
    }

    private fun scheduleNotificationFollowSending(
        notificationTime: LocalTime,
        user: User,
        title: String,
        message: String
    ) {
        val notificationDateTime = LocalDateTime.now().withHour(notificationTime.hour)
            .withMinute(notificationTime.minute)
            .withSecond(0)
            .withNano(0)

        val notification = Notification.builder()
            .user(user)
            .notificationTime(notificationTime)
            .title(title)
            .message(message)
            .build()

        sendNotificationAsync(notification, notificationDateTime)
    }

    private fun sendNotificationAsync(notification: Notification, notificationTime: LocalDateTime) {
        if (notification.isEmailEnabled) {
            notificationSender.sendEmail(notification.user, notification.title, notification.message)
        }
        if (notification.isPushEnabled) {
            notificationSender.sendPush(notification.user, notification.title, notification.message, notificationTime)
        }
    }
}
