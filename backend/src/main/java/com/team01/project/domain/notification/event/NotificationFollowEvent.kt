package com.team01.project.domain.notification.event

import com.team01.project.domain.user.entity.User
import org.springframework.context.ApplicationEvent
import java.time.LocalTime

class NotificationFollowEvent(
    source: Any,
    val time: LocalTime,
    val toUser: User,
    val fromUser: User
) : ApplicationEvent(source)
