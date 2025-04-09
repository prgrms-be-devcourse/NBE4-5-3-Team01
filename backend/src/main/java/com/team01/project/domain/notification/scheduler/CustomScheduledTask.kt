package com.team01.project.domain.notification.scheduler

import java.time.LocalTime
import java.util.concurrent.ScheduledFuture

data class CustomScheduledTask(
    val futureTask: ScheduledFuture<*>,
    val scheduledTime: LocalTime
)
