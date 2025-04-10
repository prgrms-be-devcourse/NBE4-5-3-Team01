package com.team01.project.domain.payment.controller

import com.team01.project.domain.payment.dto.TossPaymentRequest
import com.team01.project.domain.payment.dto.TossSubscriptionRequest
import com.team01.project.domain.payment.service.TossService
import com.team01.project.domain.user.service.MembershipService
import com.team01.project.global.dto.RsData
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payment")
class PaymentController(
    private val tossService: TossService,
    private val membershipService: MembershipService
) {
    @PostMapping("/confirm")
    fun confirmPayment(
        @AuthenticationPrincipal user: OAuth2User,
        @RequestBody request: TossPaymentRequest
    ): RsData<String> {
        tossService.confirmPayment(request)
            ?: return RsData("500", "결제 승인 실패", null)

        membershipService.saveOneTimePurchase(
            userId = user.getAttribute("id")!!,
            orderId = request.orderId
        )

        return RsData("200", "결제 및 멤버십 등록 성공", null)
    }

    @PostMapping("/subscribe")
    fun subscribe(
        @AuthenticationPrincipal user: OAuth2User,
        @RequestBody request: TossSubscriptionRequest
    ): RsData<String> {
        user.getAttribute<String>("id")
            ?: return RsData("401", "로그인 정보가 유효하지 않습니다.", null)

        val billingResponse = tossService.processSubscription(request)
            ?: return RsData("500", "billingKey 발급 실패", null)

        val success = membershipService.upgradeMembership(
            customerKey = billingResponse.customerKey,
            billingKey = billingResponse.billingKey
        )

        return if (success) {
            RsData("200", "결제 및 프리미엄 업그레이드 성공", null)
        } else {
            RsData("500", "결제 실패", null)
        }
    }
}
