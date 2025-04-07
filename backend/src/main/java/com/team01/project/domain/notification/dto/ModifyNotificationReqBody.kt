package com.team01.project.domain.notification.dto

import jakarta.validation.constraints.NotNull
import java.time.LocalTime

data class ModifyNotificationReqBody(
    @field:NotNull val notificationTime: LocalTime
)
