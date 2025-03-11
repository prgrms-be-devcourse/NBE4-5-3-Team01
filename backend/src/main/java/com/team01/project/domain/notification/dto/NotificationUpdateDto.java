package com.team01.project.domain.notification.dto;

public record NotificationUpdateDto(
		Long notificationId,
		Boolean isEmailNotificationEnabled,
		Boolean isPushNotificationEnabled
) {
}