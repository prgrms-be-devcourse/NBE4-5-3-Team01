package com.team01.project.domain.user.dto;

import com.team01.project.domain.user.entity.User;

public record SimpleUserResponse(
	String id,
	String name,
	String nickName,
	String profileImg
) {

	public static SimpleUserResponse from(User user) {
		return new SimpleUserResponse(user.getId(), user.getName(), user.getNickName(), user.getImage());
	}
}
