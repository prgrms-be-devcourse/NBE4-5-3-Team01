package com.team01.project.domain.notification.event

import org.springframework.context.ApplicationEvent
import java.time.LocalTime

class NotificationUpdatedEvent(
    source: Any,
    val updateTime: LocalTime
) : ApplicationEvent(source)
