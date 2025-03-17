package com.team01.project.domain.notification.entity;

import java.time.LocalTime;

import com.team01.project.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String message;

	private LocalTime notificationTime;

	@Builder.Default
	private boolean isEmailEnabled = true;

	@Builder.Default
	private boolean isPushEnabled = true;


	public void updateNotificationTime(LocalTime notificationTime) {
		this.notificationTime = notificationTime;
	}

	public void updateNotificationSettings(Boolean emailEnabled, Boolean pushEnabled) {
		if (emailEnabled != null) {
			this.isEmailEnabled = emailEnabled;
		}
		if (pushEnabled != null) {
			this.isPushEnabled = pushEnabled;
		}
	}
}
