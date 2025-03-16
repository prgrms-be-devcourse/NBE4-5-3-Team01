package com.team01.project.domain.follow.controller.dto;

import com.team01.project.domain.follow.entity.Follow;
import com.team01.project.domain.user.dto.SimpleUserResponse;
import com.team01.project.domain.user.entity.User;

public record FollowResponse(
	SimpleUserResponse user,
	Boolean isFollowing,
	Boolean isFollower
) {

	public static FollowResponse of(User user, Boolean isFollowing, Boolean isFollower) {
		return new FollowResponse(
			SimpleUserResponse.from(user),
			isFollowing,
			isFollower
		);
	}
}
