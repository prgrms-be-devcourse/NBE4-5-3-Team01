package com.team01.project.domain.notification.controller;

import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.notification.dto.SubscriptionDto;
import com.team01.project.domain.notification.entity.Subscription;
import com.team01.project.domain.notification.repository.SubscriptionRepository;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.global.dto.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Subscription", description = "푸시 구독 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/push")
public class SubscriptionController {

	private final SubscriptionRepository subscriptionRepository;
	private final UserRepository userRepository;

	// 푸시 구독 정보 저장 (userId와 endpoint 모두 체크)
	@Operation(summary = "푸시 구독 정보 저장", description = "사용자의 푸시 구독 정보 저장")
	@PostMapping("/subscribe")
	public RsData<Void> subscribe(
			@RequestBody SubscriptionDto dto, @AuthenticationPrincipal OAuth2User user) {
		String userId = user.getName();

		// userId 기반 구독 정보 조회
		Optional<Subscription> subscriptionByUser = subscriptionRepository.findByUserId(userId);
		// endpoint 기반 구독 정보 조회
		Optional<Subscription> subscriptionByEndpoint = subscriptionRepository.findByEndpoint(dto.getEndpoint());

		Subscription subscription = null;
		User userEntity = userRepository.findById(userId).orElse(null);

		if (subscriptionByUser.isPresent() && subscriptionByEndpoint.isPresent()) {
			// 만약 userId와 endpoint 모두에 해당하는 구독 정보가 있고, 서로 다른 엔티티라면 병합 처리
			subscription = subscriptionByUser.get();
			if (!subscription.equals(subscriptionByEndpoint.get())) {
				// userId의 구독 정보를 업데이트하고, 중복된 endpoint 엔티티는 삭제
				subscription.update(dto.getEndpoint(), dto.getKeys().getP256dh(), dto.getKeys().getAuth());
				subscriptionRepository.delete(subscriptionByEndpoint.get());
			} else {
				// 두 조회 결과가 동일한 경우 그냥 업데이트
				subscription.update(dto.getEndpoint(), dto.getKeys().getP256dh(), dto.getKeys().getAuth());
			}
		} else if (subscriptionByUser.isPresent()) {    // userId 같은 것만 있다면
			subscription = subscriptionByUser.get();
			subscription.update(dto.getEndpoint(), dto.getKeys().getP256dh(), dto.getKeys().getAuth());
		} else if (subscriptionByEndpoint.isPresent()) {    // endpoint 같은 것만 있다면
			subscription = subscriptionByEndpoint.get();
			subscription.updateWithUser(
					userEntity, dto.getEndpoint(), dto.getKeys().getP256dh(), dto.getKeys().getAuth());
		} else {
			// 신규 구독 정보 생성
			subscription = Subscription.builder()
					.user(userEntity)
					.endpoint(dto.getEndpoint())
					.p256dh(dto.getKeys().getP256dh())
					.auth(dto.getKeys().getAuth())
					.build();
		}

		subscriptionRepository.save(subscription);

		return new RsData<>("200-1", "구독 저장 성공");
	}
}
