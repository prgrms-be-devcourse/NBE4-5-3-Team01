package com.team01.project.domain.music.service

import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.music.repository.MusicRepository
import com.team01.project.domain.music.repository.findByIdOrThrow
import com.team01.project.domain.musicrecord.repository.MusicRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class MusicService(
    private val musicRepository: MusicRepository,
    private val musicRecordRepository: MusicRecordRepository
) {

    @Transactional
    fun saveMusic(music: Music): Music {
        return musicRepository.findById(music.id)
            .map { existingMusic ->
                if (!existingMusic.isSameAs(music)) {
                    existingMusic.updateMusic(music)
                }
                existingMusic
            }
            .orElseGet { musicRepository.save(music) }
    }

    @Transactional
    fun saveAllMusic(musicList: List<Music>): List<Music> {
        return musicList.map { saveMusic(it) }
    }

    @Transactional(readOnly = true)
    fun getAllMusic(): List<Music> {
        return musicRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun getMusicById(id: String): Music {
        return musicRepository.findByIdOrThrow(id)
    }

    @Transactional
    fun deleteMusic(id: String) {
        if (!musicRepository.existsById(id)) {
            throw IllegalArgumentException("해당 ID의 음악을 찾을 수 없습니다: $id")
        }
        musicRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getRandomRecentMusic(userId: String): Optional<Music> {
        val recentCalendarDateIdOpt = musicRecordRepository.findRecentCalendarDateIdByUserId(userId)

        if (recentCalendarDateIdOpt.isEmpty) {
            return Optional.empty()
        }

        val recentCalendarDateId = recentCalendarDateIdOpt.get()
        val musicIds = musicRecordRepository.findMusicIdsByCalendarDateId(recentCalendarDateId)

        if (musicIds.isEmpty()) {
            return Optional.empty()
        }

        val randomMusicId = musicIds.random()
        return musicRepository.findById(randomMusicId)
    }
}
