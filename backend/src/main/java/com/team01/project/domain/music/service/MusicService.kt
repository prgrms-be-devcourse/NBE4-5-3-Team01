package com.team01.project.domain.music.service

import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.music.repository.MusicRepository
import com.team01.project.domain.musicrecord.repository.MusicRecordRepository
import com.team01.project.global.exception.MusicErrorCode
import com.team01.project.global.exception.MusicException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

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
        return musicRepository.findById(id)
            .orElseThrow { MusicException(MusicErrorCode.NOT_FOUND) }
    }

    @Transactional
    fun deleteMusic(id: String) {
        if (!musicRepository.existsById(id)) {
            throw MusicException(MusicErrorCode.NOT_FOUND)
        }
        musicRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getRandomRecentMusic(userId: String): Optional<Music> {
        val recentCalendarDateId = musicRecordRepository
            .findRecentCalendarDateIdByUserId(userId)
            .orElse(null) ?: return Optional.empty()

        val randomMusicId = musicRecordRepository
            .findMusicIdsByCalendarDateId(recentCalendarDateId)
            .takeIf { it.isNotEmpty() }
            ?.random() ?: return Optional.empty()

        return musicRepository.findById(randomMusicId)
    }
}
