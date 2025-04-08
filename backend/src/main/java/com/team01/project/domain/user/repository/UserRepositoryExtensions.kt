package com.team01.project.domain.user.repository

import com.team01.project.domain.user.entity.User

fun UserRepository.findByIdOrThrow(id: String): User =
    this.findById(id).orElseThrow {
        IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: $id")
    }
