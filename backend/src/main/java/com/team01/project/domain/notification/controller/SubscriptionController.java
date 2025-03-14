package com.team01.project.domain.notification.controller;

import java.util.List;

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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/push")
public class SubscriptionController {

	private final SubscriptionRepository subscriptionRepository;
	private final PushNotificationService pushNotificationService;
	private final UserRepository userRepository;

	// 푸시 구독 정보 저장
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

	// 푸시 알림 테스트
	@PostMapping("/notify")
	public ResponseEntity<String> notifyAll(@RequestBody NotificationRequest request) {
		List<Subscription> subscriptions = subscriptionRepository.findAll();
		for (Subscription sub : subscriptions) {
			try {
				pushNotificationService.sendPush(
						sub.getEndpoint(),
						sub.getP256dh(),
						sub.getAuth(),
						request.getPayload()
				);
			} catch (Exception e) {
				// 실패한 구독은 로그 기록 (실제 서비스에서는 재시도나 구독 삭제 로직 고려)
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok("모든 구독자에게 알림 전송 시도");
	}

	// 요청 Body를 받을 DTO
	@Setter
	@Getter
	public static class NotificationRequest {
		private String payload;

	}
}
