package com.team01.project.domain.music.repository

import com.team01.project.domain.music.entity.Music
import org.springframework.data.jpa.repository.JpaRepository

interface MusicRepository : JpaRepository<Music, String>
