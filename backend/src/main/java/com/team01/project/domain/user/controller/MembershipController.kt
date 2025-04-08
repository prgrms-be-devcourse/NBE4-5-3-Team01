package com.team01.project.domain.user.controller

import MembershipResponse
import com.team01.project.domain.user.service.MembershipService
import com.team01.project.global.dto.RsData
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/membership")
class MembershipController(
    private val membershipService: MembershipService
) {
    @GetMapping("/my")
    @Operation(summary = "현재 로그인한 사용자의 요금제 조회", description = "사용자의 요금제 등급, 기간, 자동 갱신 여부 반환")
    fun getMyMembership(
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<MembershipResponse> {
        val userId = user.getAttribute<String>("id")
            ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다.")

        val membership = membershipService.getCurrentUserMembership(userId)

        return RsData(
            code = "200-1",
            msg = "요금제 정보 조회 성공",
            data = membership
        )
    }
}
