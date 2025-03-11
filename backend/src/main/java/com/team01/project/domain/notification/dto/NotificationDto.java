package com.team01.project.domain.notification.dto;

import java.time.LocalTime;

import com.team01.project.domain.notification.entity.Notification;

public record NotificationDto(
		Long id,
		String userId,
		String title,
		String message,
		LocalTime notificationTime
) {
	public NotificationDto(Notification notification) {
		this(
				notification.getId(),
				notification.getUser().getId(),
				notification.getTitle(),
				notification.getMessage(),
				notification.getNotificationTime()
		);
	}
}
