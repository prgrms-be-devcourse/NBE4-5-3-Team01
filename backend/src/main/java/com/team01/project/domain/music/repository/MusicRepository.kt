package com.team01.project.domain.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team01.project.domain.music.entity.Music;

import jakarta.persistence.EntityNotFoundException;

public interface MusicRepository extends JpaRepository<Music, String> {

	default Music findByIdOrThrow(String id) {
		return findById(id).orElseThrow(()
			-> new EntityNotFoundException("해당 ID의 음악을 찾을 수 없습니다: " + id));
	}

}