package com.team01.project.domain.notification.event;

import java.time.LocalTime;

import org.springframework.context.ApplicationEvent;

import com.team01.project.domain.user.entity.User;

import lombok.Getter;

@Getter
public class NotificationFollowEvent extends ApplicationEvent {
	private final LocalTime time;
	private final User toUser;
	private final User fromUser;

	public NotificationFollowEvent(Object source, LocalTime time, User toUser, User fromUser) {
		super(source);
		this.time = time;
		this.toUser = toUser;
		this.fromUser = fromUser;
	}
}
