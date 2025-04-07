package com.team01.project.domain.notification.event;

import java.time.LocalTime;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;


@Getter
public class NotificationUpdatedEvent extends ApplicationEvent {
	private final LocalTime updateTime;

	public NotificationUpdatedEvent(Object source, LocalTime updateTime) {
		super(source);
		this.updateTime = updateTime;
	}
}
