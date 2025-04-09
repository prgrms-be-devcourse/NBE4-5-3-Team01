package com.team01.project.musicrecord.service

import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.calendardate.repository.CalendarDateRepository
import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.music.repository.MusicRepository
import com.team01.project.domain.musicrecord.entity.MusicRecord
import com.team01.project.domain.musicrecord.entity.MusicRecordId
import com.team01.project.domain.musicrecord.repository.MusicRecordRepository
import com.team01.project.domain.musicrecord.service.MusicRecordService
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.permission.CalendarPermission
import com.team01.project.global.permission.PermissionService
import com.team01.project.util.TestDisplayNameGenerator
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Optional

@DisplayNameGeneration(TestDisplayNameGenerator::class)
class MusicRecordServiceTest {
    private val musicRecordRepository: MusicRecordRepository = mockk()
    private val calendarDateRepository: CalendarDateRepository = mockk()
    private val musicRepository: MusicRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val permissionService: PermissionService = mockk()

    private val musicRecordService = MusicRecordService(
        musicRecordRepository,
        calendarDateRepository,
        musicRepository,
        userRepository,
        permissionService
    )

    private val calendarDateId = 1L
    private val userId = "test-user"

    private val user = User(id = userId, email = "email", name = "name")

    private val calendarDate = CalendarDate(
        id = calendarDateId,
        user = user,
        date = LocalDate.of(2025, 3, 1),
        memo = "memo 1"
    )

    private val music1 = Music(
        id = "1",
        name = "song 1",
        singer = "singer 1",
        singerId = "singer 1 id",
        albumImage = "album image 1"
    )

    private val music2 = Music(
        id = "2",
        name = "song 2",
        singer = "singer 2",
        singerId = "singer 2 id",
        albumImage = "album image 2"
    )

    private val record1 = MusicRecord(
        id = MusicRecordId(calendarDateId, "1"),
        calendarDate = calendarDate,
        music = music1
    )

    private val record2 = MusicRecord(
        id = MusicRecordId(calendarDateId, "2"),
        calendarDate = calendarDate,
        music = music2
    )

    private val musicRecords = listOf(record1, record2)

    @Test
    fun `기록한 음악 목록을 캘린더 아이디로 조회한다`() {
        // given
        every { calendarDateRepository.findById(calendarDateId) } returns Optional.of(calendarDate)
        every { musicRecordRepository.findByCalendarDate(calendarDate) } returns musicRecords

        // when
        val result = musicRecordService.findMusicsByCalendarDateId(calendarDateId)

        // then
        assertThat(result).hasSize(musicRecords.size)
        assertTrue(result.contains(music1))
        assertTrue(result.contains(music2))
    }

    @Test
    fun `음악 기록 하나를 캘린더 아이디로 조회한다`() {
        // given
        every { calendarDateRepository.findById(calendarDateId) } returns Optional.of(calendarDate)
        every { musicRecordRepository.findTopByCalendarDate(calendarDate) } returns Optional.of(musicRecords[0])

        // when
        val result = musicRecordService.findOneByCalendarDateId(calendarDateId)

        // then
        assertThat(result.isPresent)
        assertThat(result.get().music.id).isEqualTo(musicRecords[0].music.id)
    }

    @Test
    fun `음악 기록을 업데이트한다`() {
        // given
        val commonMusicId: String = musicRecords[0].music.id
        val musicIdToAdd = "3"
        val musicNameToAdd = "Song 3"
        val newMusicIds = listOf(commonMusicId, musicIdToAdd)
        val musicToAdd = Music(
            id = musicIdToAdd,
            name = musicNameToAdd,
            singer = "singer 3",
            singerId = "singer 3 id",
            albumImage = "album image 3"
        )

        every { calendarDateRepository.findWithOwnerById(calendarDateId) } returns Optional.of(calendarDate)
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { permissionService.checkPermission(any<User>(), any<User>()) } returns CalendarPermission.EDIT
        every { musicRecordRepository.findByCalendarDate(calendarDate) } returns musicRecords
        every { musicRepository.findById(musicIdToAdd) } returns Optional.of(musicToAdd)
        every { musicRecordRepository.deleteAll(any<List<MusicRecord>>()) } just Runs
        every { musicRecordRepository.saveAll(any<List<MusicRecord>>()) } returns emptyList()

        // when
        musicRecordService.updateMusicRecords(calendarDateId, userId, newMusicIds)

        // then
        verify(exactly = 1) { musicRecordRepository.deleteAll(any<List<MusicRecord>>()) }
        verify(exactly = 1) { musicRecordRepository.saveAll(any<List<MusicRecord>>()) }
    }
}
