package com.team01.project.domain.notification.service

import java.time.LocalTime
import java.util.concurrent.ScheduledFuture

data class CustomScheduledTask(
    val futureTask: ScheduledFuture<*>,
    val scheduledTime: LocalTime
)
