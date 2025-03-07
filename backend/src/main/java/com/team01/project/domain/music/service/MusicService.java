package com.team01.project.domain.music.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.music.entity.Music;
import com.team01.project.domain.music.repository.MusicRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicService {

	private final MusicRepository musicRepository;

	@Transactional
	public Music saveMusic(Music music) {
		return musicRepository.findById(music.getId())
			.orElseGet(() -> musicRepository.save(music));
	}

	@Transactional(readOnly = true)
	public List<Music> getAllMusic() {
		return musicRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Music getMusicById(String id) {
		return musicRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 음악을 찾을 수 없습니다: " + id));
	}

	@Transactional
	public void deleteMusic(String id) {
		if (!musicRepository.existsById(id)) {
			throw new IllegalArgumentException("해당 ID의 음악을 찾을 수 없습니다: " + id);
		}
		musicRepository.deleteById(id);
	}

}
