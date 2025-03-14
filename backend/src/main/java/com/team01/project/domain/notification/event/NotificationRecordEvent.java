package com.team01.project.domain.notification.event;

import java.time.LocalTime;

import org.springframework.context.ApplicationEvent;

import com.team01.project.domain.user.entity.User;

import lombok.Getter;

@Getter
public class NotificationRecordEvent extends ApplicationEvent {
	private final LocalTime time;
	private final User user;

	public NotificationRecordEvent(Object source, LocalTime time, User user) {
		super(source);
		this.time = time;
		this.user = user;
	}
}
