package com.team01.project.domain.follow.controller.dto

import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.user.dto.SimpleUserResponse

data class FollowResponse(
    val user: SimpleUserResponse,
    val isFollowing: Status,
    val isFollower: Status
)
