package com.team01.project.domain.notification.entity;

import com.team01.project.domain.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "USER_ID", nullable = false)
	private User user;

	private String endpoint;

	// 구독 정보 안의 키 값들
	private String p256dh;
	private String auth;

	public void update(String endpoint, String p256dh, String auth) {
		this.endpoint = endpoint;
		this.p256dh = p256dh;
		this.auth = auth;
	}

	public void updateWithUser(User user, String endpoint, String p256dh, String auth) {
		this.user = user;
		this.endpoint = endpoint;
		this.p256dh = p256dh;
		this.auth = auth;
	}

}

