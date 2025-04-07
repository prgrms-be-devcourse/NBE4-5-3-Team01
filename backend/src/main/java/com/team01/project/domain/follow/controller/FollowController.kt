package com.team01.project.domain.follow.controller

import com.team01.project.domain.follow.controller.dto.CountFollowResponse
import com.team01.project.domain.follow.controller.dto.FollowResponse
import com.team01.project.domain.follow.service.CommandFollowService
import com.team01.project.domain.follow.service.QueryFollowService
import com.team01.project.global.dto.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Follows", description = "팔로우 API")
@RestController
@RequestMapping("/follows")
class FollowController(
    private val commandFollowService: CommandFollowService,
    private val queryFollowService: QueryFollowService
) {

    @Operation(summary = "팔로우 생성 api", description = "user-id에게 팔로우를 요청한다.")
    @PostMapping("/{user-id}")
    fun create(
        @PathVariable(name = "user-id") userId: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<Void> {
        commandFollowService.create(user.name, userId)
        return RsData(
            "201-1",
            "팔로우가 생성 되었습니다."
        )
    }

    @Operation(summary = "팔로우 삭제 api", description = "user-id에게 팔로우를 취소한다.")
    @DeleteMapping("/delete/{user-id}")
    fun delete(
        @PathVariable(name = "user-id") userId: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<Void> {
        commandFollowService.delete(user.name, userId)
        return RsData(
            "200-1",
            "팔로우가 삭제 되었습니다."
        )
    }

    @Operation(summary = "팔로우 거절 api", description = "user-id의 팔로우 요청을 거절한다.")
    @DeleteMapping("/reject/{user-id}")
    fun reject(
        @PathVariable(name = "user-id") userId: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<Void> {
        commandFollowService.delete(userId, user.name)
        return RsData(
            "200-1",
            "팔로우 요청이 거절 되었습니다."
        )
    }

    @Operation(summary = "팔로우 수락 api", description = "user-id의 팔로우 요청을 수락한다.")
    @PutMapping("/accept/{user-id}")
    fun accept(
        @PathVariable(name = "user-id") userId: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<Void> {
        commandFollowService.accept(userId, user.name)
        return RsData(
            "200-1",
            "팔로우 요청이 수락 되었습니다"
        )
    }

    @Operation(summary = "팔로잉 목록 조회 api", description = "user-id의 팔로잉 목록을 조회한다.")
    @GetMapping("/following/{user-id}")
    fun getFollowings(
        @PathVariable(name = "user-id") userId: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<List<FollowResponse>> {
        return RsData(
            "200-1",
            "팔로잉 목록 조회가 완료되었습니다.",
            queryFollowService.findFollowing(user.name, userId)
        )
    }

    @Operation(summary = "내 팔로잉 목록 조회 api", description = "로그인된 유저의 팔로잉 목록을 조회한다.")
    @GetMapping("my")
    fun getFollowings(@AuthenticationPrincipal user: OAuth2User): RsData<List<FollowResponse>> {
        return RsData(
            "200-1",
            "자신의 팔로잉 목록 조회가 완료되었습니다.",
            queryFollowService.findMyFollowing(user.name)
        )
    }

    @Operation(summary = "팔로워 요청 목록 조회 api", description = "로그인된 유저의 팔로워 요청 목록을 조회한다.")
    @GetMapping("/my/pending")
    fun getPendingList(@AuthenticationPrincipal user: OAuth2User): RsData<List<FollowResponse>> {
        return RsData(
            "200-1",
            "팔로워 요청 목록 조회가 완료되었습니다.",
            queryFollowService.findPendingList(user.name)
        )
    }

    @Operation(summary = "팔로워 목록 조회 api", description = "user-id의 팔로워 목록을 조회한다.")
    @GetMapping("/follower/{user-id}")
    fun getFollowers(
        @PathVariable(name = "user-id") userId: String,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<List<FollowResponse>> {
        return RsData(
            "200-1",
            "팔로워 목록 조회가 완료되었습니다.",
            queryFollowService.findFollower(user.name, userId)
        )
    }

    @Operation(summary = "팔로잉, 팔로워 수 조회 api", description = "user-id의 팔로잉, 팔로워 수를 조회한다.")
    @GetMapping("/count/{user-id}")
    fun getCount(@PathVariable(name = "user-id") userId: String): RsData<CountFollowResponse> {
        return RsData(
            "200-1",
            "팔로잉, 팔로워 수 조회가 완료되었습니다.",
            queryFollowService.findCount(userId)
        )
    }
}
