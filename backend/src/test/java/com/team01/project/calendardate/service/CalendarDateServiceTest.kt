package com.team01.project.calendardate.service

import com.team01.project.domain.calendardate.controller.dto.response.CalendarDateFetchResponse
import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.calendardate.repository.CalendarDateRepository
import com.team01.project.domain.calendardate.service.CalendarDateService
import com.team01.project.domain.music.entity.Music
import com.team01.project.domain.musicrecord.service.MusicRecordService
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.permission.CalendarPermission
import com.team01.project.global.permission.PermissionService
import com.team01.project.util.TestDisplayNameGenerator
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate
import java.time.YearMonth
import java.util.Optional

@DisplayNameGeneration(TestDisplayNameGenerator::class)
class CalendarDateServiceTest {
    private val calendarDateRepository: CalendarDateRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val permissionService: PermissionService = mockk()
    private val eventPublisher: ApplicationEventPublisher = mockk(relaxed = true)
    private val musicRecordService: MusicRecordService = mockk()

    private val calendarDateService = CalendarDateService(
        calendarDateRepository,
        userRepository,
        permissionService,
        eventPublisher,
        musicRecordService
    )

    @Test
    fun `먼슬리 캘린더를 유저 아이디와 날짜로 조회한다`() {
        // given
        val mockUserId = "test-user"
        val mockUser = User(id = mockUserId, email = "email", name = "name")

        val yearMonth = YearMonth.of(2025, 3)
        val start = yearMonth.atDay(1)
        val end = yearMonth.atEndOfMonth()

        val mockCalendarDates = listOf(
            CalendarDate(user = mockUser, date = LocalDate.of(2025, 3, 1), memo = "memo 1"),
            CalendarDate(user = mockUser, date = LocalDate.of(2025, 3, 31), memo = "memo 2")
        )

        every { userRepository.findById(mockUserId) } returns Optional.of(mockUser)
        every { permissionService.checkPermission(eq(mockUser), eq(mockUser)) } returns CalendarPermission.EDIT
        every { calendarDateRepository.findByUserAndDateBetween(mockUser, start, end) } returns mockCalendarDates

        // when
        val result = calendarDateService.findAllByYearAndMonth(mockUserId, mockUserId, yearMonth)

        // then
        assertThat(result).hasSize(mockCalendarDates.size)

        result.forEach {
            assertThat(it.user.id).isEqualTo(mockUserId)
        }
    }

    @Test
    fun `캘린더를 아이디로 조회한다`() {
        // given
        val mockCalendarDateId = 1L
        val mockUserId = "test-user"
        val mockUser = User(id = mockUserId, email = "email", name = "name")
        val mockCalendarDate = getMockCalendarDate()
        val musics = emptyList<Music>()
        val mockResponseDto = CalendarDateFetchResponse.of(mockCalendarDate, musics, CalendarPermission.EDIT)

        every { calendarDateRepository.findWithOwnerById(mockCalendarDateId) } returns Optional.of(mockCalendarDate)
        every { userRepository.findById(mockUserId) } returns Optional.of(mockUser)
        every { permissionService.checkPermission(any<User>(), any<User>()) } returns CalendarPermission.EDIT
        every { musicRecordService.findMusicsByCalendarDateId(mockCalendarDateId) } returns musics

        // when
        val result = calendarDateService.findCalendarDateWithMusics(mockCalendarDateId, mockUserId)

        // then
        assertThat(result).isEqualTo(mockResponseDto)
    }

    @Test
    fun `캘린더 메모를 수정한다`() {
        // given
        val mockCalendarDateId = 1L
        val newMemo = "new memo"
        val mockUserId = "test-user"
        val mockUser = User(id = mockUserId, email = "email", name = "name")
        val mockDate = LocalDate.of(2025, 3, 1)
        val mockCalendarDate = getMockCalendarDate(mockDate)

        every { calendarDateRepository.findWithOwnerById(mockCalendarDateId) } returns Optional.of(mockCalendarDate)
        every { userRepository.findById(mockUserId) } returns Optional.of(mockUser)
        every { permissionService.checkPermission(any<User>(), any<User>()) } returns CalendarPermission.EDIT

        // when
        calendarDateService.updateMemo(mockCalendarDateId, mockUserId, newMemo)

        // then
        assertThat(mockCalendarDate.memo).isEqualTo(newMemo)
    }

    @Test
    fun `캘린더를 유저 아이디로 생성한다`() {
        // given
        val mockDate = LocalDate.of(2025, 3, 1)
        val mockMemo = "memo"
        val mockUserId = "test-user"
        val mockUser = User(id = mockUserId, email = "email", name = "name")

        every { userRepository.findById(mockUserId) } returns Optional.of(mockUser)
        every { calendarDateRepository.existsByUserAndDate(mockUser, mockDate) } returns false
        every { calendarDateRepository.save(any<CalendarDate>()) } answers { firstArg() }

        // when
        val result = calendarDateService.create(mockUserId, mockDate, mockMemo)

        // then
        assertThat(result).isNotNull
        assertThat(result.user.id).isEqualTo(mockUserId)
        assertThat(result.date).isEqualTo(mockDate)
        assertThat(result.memo).isEqualTo(mockMemo)
    }

    private fun getMockCalendarDate(): CalendarDate {
        return CalendarDate(
            id = 1,
            user = User(id = "id", email = "email", name = "name"),
            date = LocalDate.of(2025, 3, 1),
            memo = "memo 1"
        )
    }

    private fun getMockCalendarDate(date: LocalDate): CalendarDate {
        return CalendarDate(
            id = 1,
            user = User(id = "id", email = "email", name = "name"),
            date = date,
            memo = "memo 1"
        )
    }
}
