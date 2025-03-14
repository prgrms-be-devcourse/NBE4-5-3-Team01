package com.team01.project.domain.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionDto {
	private String endpoint;
	private Keys keys;

	@Getter
	@Setter
	public static class Keys {
		private String p256dh;
		private String auth;

	}
}
