package com.team01.project.domain.notification.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CreateNotificationReqBody(
		@NotNull String userId,
		@NotBlank String message,
		@NotNull LocalTime notificationTime
) {
}
