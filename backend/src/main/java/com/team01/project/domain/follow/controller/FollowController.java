package com.team01.project.domain.follow.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.follow.controller.dto.CountFollowResponse;
import com.team01.project.domain.follow.controller.dto.FollowResponse;
import com.team01.project.domain.follow.service.CommandFollowService;
import com.team01.project.domain.follow.service.QueryFollowService;
import com.team01.project.global.dto.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Follows", description = "팔로우 API")
@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
public class FollowController {

	private final CommandFollowService commandFollowService;
	private final QueryFollowService queryFollowService;

	@Operation(summary = "팔로우 생성 api", description = "user-id에게 팔로우를 요청한다.")
	@PostMapping("/{user-id}")
	public RsData<Void> create(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		commandFollowService.create(user.getName(), userId);
		return new RsData<>(
			"201-1",
			"팔로우가 생성 되었습니다."
		);
	}

	@Operation(summary = "팔로우 삭제 api", description = "user-id에게 팔로우를 취소한다.")
	@DeleteMapping("/delete/{user-id}")
	public RsData<Void> delete(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		commandFollowService.delete(user.getName(), userId);
		return new RsData<>(
			"200-1",
			"팔로우가 삭제 되었습니다."
		);
	}

	@Operation(summary = "팔로우 거절 api", description = "user-id의 팔로우 요청을 거절한다.")
	@DeleteMapping("/reject/{user-id}")
	public RsData<Void> reject(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		commandFollowService.delete(userId, user.getName());
		return new RsData<>(
			"200-1",
			"팔로우 요청이 거절 되었습니다."
		);
	}

	@Operation(summary = "팔로우 수락 api", description = "user-id의 팔로우 요청을 수락한다.")
	@PutMapping("/accept/{user-id}")
	public RsData<Void> accept(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		commandFollowService.accept(userId, user.getName());
		return new RsData<>(
			"200-1",
			"팔로우 요청이 수락 되었습니다"
		);
	}

	@Operation(summary = "팔로잉 목록 조회 api", description = "user-id의 팔로잉 목록을 조회한다.")
	@GetMapping("/following/{user-id}")
	public RsData<List<FollowResponse>> getFollowings(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		return new RsData<>(
			"200-1",
			"팔로잉 목록 조회가 완료되었습니다.",
			queryFollowService.findFollowing(user.getName(), userId)
		);
	}

	@Operation(summary = "내 팔로잉 목록 조회 api", description = "로그인된 유저의 팔로잉 목록을 조회한다.")
	@GetMapping("my")
	public RsData<List<FollowResponse>> getFollowings(@AuthenticationPrincipal OAuth2User user) {
		return new RsData<>(
			"200-1",
			"자신의 팔로잉 목록 조회가 완료되었습니다.",
			queryFollowService.findMyFollowing(user.getName())
		);
	}

	@Operation(summary = "팔로워 요청 목록 조회 api", description = "로그인된 유저의 팔로워 요청 목록을 조회한다.")
	@GetMapping("/my/pending")
	public RsData<List<FollowResponse>> getPendingList(@AuthenticationPrincipal OAuth2User user) {
		return new RsData<>(
			"200-1",
			"팔로워 요청 목록 조회가 완료되었습니다.",
			queryFollowService.findPendingList(user.getName())
		);
	}

	@Operation(summary = "팔로워 목록 조회 api", description = "user-id의 팔로워 목록을 조회한다.")
	@GetMapping("/follower/{user-id}")
	public RsData<List<FollowResponse>> getFollowers(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		return new RsData<>(
			"200-1",
			"팔로워 목록 조회가 완료되었습니다.",
			queryFollowService.findFollower(user.getName(), userId)
		);
	}

	@Operation(summary = "팔로잉, 팔로워 수 조회 api", description = "user-id의 팔로잉, 팔로워 수를 조회한다.")
	@GetMapping("/count/{user-id}")
	public RsData<CountFollowResponse> getCount(@PathVariable(name = "user-id") String userId) {
		return new RsData<>(
			"200-1",
			"팔로잉, 팔로워 수 조회가 완료되었습니다.",
			queryFollowService.findCount(userId)
		);
	}

	// @Operation(summary = "맞팔로우 확인 api", description = "user-id와 맞팔로우 여부를 확인한다.")
	// @GetMapping("/check/{user-id}")
	// public RsData<Boolean> checkMutualFollow(
	// 	@PathVariable(name = "user-id") String userId,
	// 	@AuthenticationPrincipal OAuth2User user
	// ) {
	// 	return new RsData<>(
	// 		"200-1",
	// 		"맞팔로우 여부 조회가 완료되었습니다.",
	// 		queryFollowService.checkMutualFollow(user.getName(), userId)
	// 	);
	// }
}
