package com.team01.project.domain.follow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.follow.controller.dto.CountFollowResponse;
import com.team01.project.domain.follow.controller.dto.FollowResponse;
import com.team01.project.domain.follow.service.CommandFollowService;
import com.team01.project.domain.follow.service.QueryFollowService;

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
	@ResponseStatus(HttpStatus.CREATED)
	public void create(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		commandFollowService.create(user.getName(), userId);
	}

	@Operation(summary = "팔로우 삭제 api", description = "user-id에게 팔로우를 취소한다.")
	@DeleteMapping("/{user-id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		commandFollowService.delete(user.getName(), userId);
	}

	@Operation(summary = "팔로잉 목록 조회 api", description = "user-id의 팔로잉 목록을 조회한다.")
	@GetMapping("/following/{user-id}")
	public List<FollowResponse> getFollowings(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		return queryFollowService.findFollowing(user.getName(), userId);
	}

	@Operation(summary = "팔로워 목록 조회 api", description = "user-id의 팔로워 목록을 조회한다.")
	@GetMapping("/follower/{user-id}")
	public List<FollowResponse> getFollowers(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		return queryFollowService.findFollower(user.getName(), userId);
	}

	@Operation(summary = "팔로잉, 팔로워 수 조회 api", description = "user-id의 팔로잉, 팔로워 수를 조회한다.")
	@GetMapping("/count/{user-id}")
	public CountFollowResponse getCount(@PathVariable(name = "user-id") String userId) {
		return queryFollowService.findCount(userId);
	}

	@Operation(summary = "맞팔로우 확인 api", description = "user-id와 맞팔로우 여부를 확인한다.")
	@GetMapping("/check/{user-id}")
	public Boolean checkMutualFollow(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		return queryFollowService.checkMutualFollow(user.getName(), userId);
	}
}
