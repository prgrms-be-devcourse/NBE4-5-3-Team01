package com.team01.project.domain.follow.controller.dto;

import com.team01.project.domain.follow.entity.type.Status;
import com.team01.project.domain.user.dto.SimpleUserResponse;
import com.team01.project.domain.user.entity.User;

public record FollowResponse(
	SimpleUserResponse user,
	Status isFollowing,
	Status isFollower
) {

	public static FollowResponse of(User user, Status isFollowing, Status isFollower) {
		return new FollowResponse(
			SimpleUserResponse.from(user),
			isFollowing,
			isFollower
		);
	}
}
