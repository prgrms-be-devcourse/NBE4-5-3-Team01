package com.team01.project.domain.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.notification.dto.SubscriptionDto;
import com.team01.project.domain.notification.entity.Subscription;
import com.team01.project.domain.notification.repository.SubscriptionRepository;
import com.team01.project.domain.notification.service.PushNotificationService;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Subscription", description = "푸시 구독 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/push")
public class SubscriptionController {

	private final SubscriptionRepository subscriptionRepository;
	private final PushNotificationService pushNotificationService;
	private final UserRepository userRepository;

	// 푸시 구독 정보 저장
	@Operation(summary = "푸시 구독 정보 저장", description = "사용자의 푸시 구독 정보 저장")
	@PostMapping("/subscribe")
	public ResponseEntity<String> subscribe(
			@RequestBody SubscriptionDto dto, @AuthenticationPrincipal OAuth2User user) {
		String userId = user.getName();
		// userId를 기준으로 중복 체크
		if (subscriptionRepository.findByUserId(userId).isPresent()) {
			return ResponseEntity.ok("구독 이미 존재함");
		}

		User user1 = userRepository.findById(userId).orElse(null);
		Subscription subscription = Subscription.builder()
				.user(user1)
				.endpoint(dto.getEndpoint())
				.p256dh(dto.getKeys().getP256dh())
				.auth(dto.getKeys().getAuth())
				.build();
		subscriptionRepository.save(subscription);
		return ResponseEntity.ok("구독 저장 성공");
	}
}
