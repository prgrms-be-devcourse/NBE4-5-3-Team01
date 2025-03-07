package com.team01.project.domain.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team01.project.domain.music.entity.Music;

public interface MusicRepository extends JpaRepository<Music, String> {
}
