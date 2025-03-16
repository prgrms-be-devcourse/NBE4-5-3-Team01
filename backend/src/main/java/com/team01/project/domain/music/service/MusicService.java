package com.team01.project.domain.music.service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.music.entity.Music;
import com.team01.project.domain.music.repository.MusicRepository;
import com.team01.project.domain.musicrecord.repository.MusicRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicService {

	private final MusicRepository musicRepository;
	private final MusicRecordRepository musicRecordRepository;

	@Transactional
	public Music saveMusic(Music music) {
		return musicRepository.findById(music.getId())
			.map(existingMusic -> {
				if (!existingMusic.isSameAs(music)) {
					existingMusic.updateMusic(music);
				}
				return existingMusic;
			})
			.orElseGet(() -> musicRepository.save(music));
	}

	@Transactional
	public List<Music> saveAllMusic(List<Music> musicList) {
		return musicList.stream()
			.map(this::saveMusic)
			.collect(Collectors.toList());
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

	@Transactional(readOnly = true)
	public Music getRandomRecentMusic(String userId) {
		// 1. 가장 최근 calendar_date ID 조회
		Long recentCalendarDateId = musicRecordRepository.findRecentCalendarDateIdByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자의 캘린더 기록이 없습니다."));

		// 2. 해당 날짜의 음악 기록 가져오기
		List<String> musicIds = musicRecordRepository.findMusicIdsByCalendarDateId(recentCalendarDateId);

		if (musicIds.isEmpty()) {
			throw new IllegalArgumentException("가장 최근 캘린더 날짜에 음악 기록이 없습니다.");
		}

		// 3. 랜덤으로 하나 선택
		Random random = new Random();
		String randomMusicId = musicIds.get(random.nextInt(musicIds.size()));

		// 4. 선택된 음악 정보 반환 (존재하지 않으면 예외 발생)
		return musicRepository.findById(randomMusicId)
			.orElseThrow(() -> new IllegalArgumentException("해당 음악을 찾을 수 없습니다."));
	}
}
