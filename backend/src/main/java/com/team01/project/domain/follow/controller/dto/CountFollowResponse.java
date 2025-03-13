package com.team01.project.domain.follow.controller.dto;

public record CountFollowResponse(
	Long followingCount,
	Long followerCount
) {

	public static CountFollowResponse of(Long followingCount, Long followerCount) {
		return new CountFollowResponse(followingCount, followerCount);
	}
}
