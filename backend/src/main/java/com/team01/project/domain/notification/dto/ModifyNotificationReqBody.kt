package com.team01.project.domain.notification.dto

import java.time.LocalTime
import jakarta.validation.constraints.NotNull

data class ModifyNotificationReqBody(
	@field:NotNull val notificationTime: LocalTime
)
