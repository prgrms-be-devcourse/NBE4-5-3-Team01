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

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
public class FollowController {

	private final CommandFollowService commandFollowService;
	private final QueryFollowService queryFollowService;

	@PostMapping("/{user-id}")
	@ResponseStatus(HttpStatus.CREATED)
	public void create(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		commandFollowService.create(user.getName(), userId);
	}

	@DeleteMapping("/{user-id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		commandFollowService.delete(user.getName(), userId);
	}

	@GetMapping("/following/{user-id}")
	public List<FollowResponse> getFollowings(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		return queryFollowService.findFollowing(user.getName(), userId);
	}

	@GetMapping("/follower/{user-id}")
	public List<FollowResponse> getFollowers(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		return queryFollowService.findFollower(user.getName(), userId);
	}

	@GetMapping("/count/{user-id}")
	public CountFollowResponse getCount(@PathVariable(name = "user-id") String userId) {
		return queryFollowService.findCount(userId);
	}

	@GetMapping("/check/{user-id}")
	public Boolean checkMutualFollow(
		@PathVariable(name = "user-id") String userId,
		@AuthenticationPrincipal OAuth2User user
	) {
		return queryFollowService.checkMutualFollow(user.getName(), userId);
	}
}
