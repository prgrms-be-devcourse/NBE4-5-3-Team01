package com.team01.project.domain.music.repository

import com.team01.project.domain.music.entity.Music

fun MusicRepository.findByIdOrThrow(id: String): Music =
    this.findById(id).orElseThrow {
        IllegalArgumentException("해당 ID의 음악을 찾을 수 없습니다: $id")
    }
