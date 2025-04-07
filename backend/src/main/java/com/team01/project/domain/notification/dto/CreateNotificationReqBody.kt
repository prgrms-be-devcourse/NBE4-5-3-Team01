package com.team01.project.domain.notification.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalTime

data class CreateNotificationReqBody(
    @field:NotNull val userId: String,
    @field:NotBlank val message: String,
    @field:NotNull val notificationTime: LocalTime
)
