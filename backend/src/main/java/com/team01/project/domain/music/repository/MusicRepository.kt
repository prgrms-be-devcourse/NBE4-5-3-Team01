package com.team01.project.domain.music.repository

import com.team01.project.domain.music.entity.Music
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.jpa.repository.JpaRepository

interface MusicRepository : JpaRepository<Music, String> {

    fun findByIdOrThrow(id: String): Music {
        return findById(id).orElseThrow {
            EntityNotFoundException("해당 ID의 음악을 찾을 수 없습니다: $id")
        }
    }
}
