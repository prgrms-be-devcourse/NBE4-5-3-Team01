package com.team01.project.music.service

import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.music.repository.MusicRepository
import com.team01.project.domain.music.service.MusicService
import com.team01.project.domain.musicrecord.repository.MusicRecordRepository
import com.team01.project.global.exception.MusicErrorCode
import com.team01.project.global.exception.MusicException
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class MusicServiceTest {

    private val musicRepository: MusicRepository = mockk()
    private val musicRecordRepository: MusicRecordRepository = mockk()
    private lateinit var musicService: MusicService

    private val sampleMusic = Music(
        id = "track123",
        name = "Test Song",
        singer = "Test Artist",
        singerId = "artist123",
        releaseDate = null,
        albumImage = "http://image.com/album.jpg",
        genre = "Pop",
        uri = "spotify:track:track123"
    )

    @BeforeEach
    fun setUp() {
        musicService = MusicService(musicRepository, musicRecordRepository)
        clearAllMocks()
    }

    @Test
    fun `saveMusic - 새 음악 저장`() {
        every { musicRepository.findById(sampleMusic.id) } returns Optional.empty()
        every { musicRepository.save(sampleMusic) } returns sampleMusic

        val saved = musicService.saveMusic(sampleMusic)

        assertEquals(sampleMusic, saved)
        verify { musicRepository.save(sampleMusic) }
    }

    @Test
    fun `saveMusic - 기존 음악 업데이트`() {
        val existingMusic = mockk<Music>(relaxed = true)

        every { existingMusic.isSameAs(sampleMusic) } returns false
        every { existingMusic.updateMusic(sampleMusic) } just Runs
        every { musicRepository.findById(sampleMusic.id) } returns Optional.of(existingMusic)

        val result = musicService.saveMusic(sampleMusic)

        assertEquals(existingMusic, result)
        verify(exactly = 0) { musicRepository.save(any()) } // 저장 X, 수정만
    }

    @Test
    fun `getAllMusic - 전체 목록 조회`() {
        every { musicRepository.findAll() } returns listOf(sampleMusic)

        val result = musicService.getAllMusic()

        assertEquals(1, result.size)
        assertEquals(sampleMusic, result[0])
    }

    @Test
    fun `getMusicById - 존재하는 음악 조회`() {
        every { musicRepository.findById(sampleMusic.id) } returns Optional.of(sampleMusic)

        val result = musicService.getMusicById(sampleMusic.id)

        assertEquals(sampleMusic, result)
    }

    @Test
    fun `getMusicById - 존재하지 않을 경우 예외`() {
        every { musicRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<MusicException> {
            musicService.getMusicById("not-found")
        }

        assertEquals(MusicErrorCode.NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `deleteMusic - 존재하지 않으면 예외`() {
        every { musicRepository.existsById(any()) } returns false

        val exception = assertThrows<MusicException> {
            musicService.deleteMusic("missing-id")
        }

        assertEquals(MusicErrorCode.NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `deleteMusic - 정상 삭제`() {
        every { musicRepository.existsById(sampleMusic.id) } returns true
        every { musicRepository.deleteById(sampleMusic.id) } just Runs

        musicService.deleteMusic(sampleMusic.id)

        verify { musicRepository.deleteById(sampleMusic.id) }
    }

    @Test
    fun `getRandomRecentMusic - 최근 음악 존재할 경우 반환`() {
        every { musicRecordRepository.findRecentCalendarDateIdByUserId("user1") } returns Optional.of(1L)
        every { musicRecordRepository.findMusicIdsByCalendarDateId(1L) } returns listOf("track123")
        every { musicRepository.findById("track123") } returns Optional.of(sampleMusic)

        val result = musicService.getRandomRecentMusic("user1")

        assertTrue(result.isPresent)
        assertEquals(sampleMusic, result.get())
    }

    @Test
    fun `getRandomRecentMusic - 최근 음악 없을 경우 빈 Optional`() {
        every { musicRecordRepository.findRecentCalendarDateIdByUserId("user1") } returns Optional.empty()

        val result = musicService.getRandomRecentMusic("user1")

        assertTrue(result.isEmpty)
    }
}
