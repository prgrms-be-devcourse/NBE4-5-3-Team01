package com.team01.project.domain.notification.controller

import com.team01.project.domain.notification.dto.SubscriptionDto
import com.team01.project.domain.notification.entity.Subscription
import com.team01.project.domain.notification.repository.SubscriptionRepository
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.dto.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Subscription", description = "푸시 구독 API")
@RestController
@RequestMapping("/push")
class SubscriptionController(
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository
) {

    @Operation(summary = "푸시 구독 정보 저장", description = "사용자의 푸시 구독 정보 저장")
    @PostMapping("/subscribe")
    fun subscribe(
        @RequestBody dto: SubscriptionDto,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<Void> {
        val userId = user.name

        val subscriptionByUser = subscriptionRepository.findByUserId(userId)
        val subscriptionByEndpoint = subscriptionRepository.findByEndpoint(dto.endpoint)
        val userEntity = userRepository.findById(userId).orElse(null)

        val subscription: Subscription = when {
            subscriptionByUser.isPresent && subscriptionByEndpoint.isPresent -> {
                val userSub = subscriptionByUser.get()
                val endpointSub = subscriptionByEndpoint.get()
                if (userSub != endpointSub) {
                    userSub.update(dto.endpoint, dto.keys.p256dh, dto.keys.auth)
                    subscriptionRepository.delete(endpointSub)
                } else {
                    userSub.update(dto.endpoint, dto.keys.p256dh, dto.keys.auth)
                }
                userSub
            }

            subscriptionByUser.isPresent -> {
                subscriptionByUser.get().apply {
                    update(dto.endpoint, dto.keys.p256dh, dto.keys.auth)
                }
            }

            subscriptionByEndpoint.isPresent -> {
                subscriptionByEndpoint.get().apply {
                    updateWithUser(userEntity, dto.endpoint, dto.keys.p256dh, dto.keys.auth)
                }
            }

            else -> Subscription.builder()
                .user(userEntity)
                .endpoint(dto.endpoint)
                .p256dh(dto.keys.p256dh)
                .auth(dto.keys.auth)
                .build()
        }

        subscriptionRepository.save(subscription)
        return RsData(code = "200-1", message = "구독 저장 성공")
    }
}
