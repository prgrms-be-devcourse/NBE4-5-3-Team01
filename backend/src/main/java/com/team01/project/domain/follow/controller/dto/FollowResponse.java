package com.team01.project.domain.follow.controller.dto;

import com.team01.project.domain.follow.entity.Follow;
import com.team01.project.domain.user.dto.SimpleUserResponse;
import com.team01.project.domain.user.entity.User;

public record FollowResponse(
	SimpleUserResponse user,
	Boolean followState
) {

	public static FollowResponse of(User user, Boolean followState) {
		return new FollowResponse(
			SimpleUserResponse.from(user),
			followState
		);
	}
}
