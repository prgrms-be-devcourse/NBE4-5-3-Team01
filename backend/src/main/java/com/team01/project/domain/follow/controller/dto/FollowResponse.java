package com.team01.project.domain.follow.controller.dto;

import com.team01.project.domain.follow.domain.Follow;

public record FollowResponse(
	Long toUserId,
	Long fromUserId
) {

	public static FollowResponse from(Follow follow) {
		return new FollowResponse(follow.getToUserId(), follow.getFromUserId());
	}
}
