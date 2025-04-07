package com.team01.project.domain.notification.dto

data class NotificationUpdateDto(
	val notificationId: Long,
	val isEmailNotificationEnabled: Boolean,
	val isPushNotificationEnabled: Boolean
)
