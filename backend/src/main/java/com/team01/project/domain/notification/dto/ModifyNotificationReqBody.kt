package com.team01.project.domain.notification.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;


public record ModifyNotificationReqBody(
		@NotNull LocalTime notificationTime
) {
}
