package com.team01.project.domain.notification.dto;

import java.time.LocalDateTime;

import com.team01.project.domain.notification.entity.NotificationList;

public record NotificationListDto(
		Long id,
		String userId,
		String message,
		LocalDateTime notificationTime
) {
	public NotificationListDto(NotificationList notificationList) {
		this(
				notificationList.getId(),
				notificationList.getUser().getId(),
				notificationList.getMessage(),
				notificationList.getNotificationTime()
		);
	}
}
