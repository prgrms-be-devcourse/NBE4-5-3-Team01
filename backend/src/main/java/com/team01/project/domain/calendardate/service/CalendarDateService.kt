package com.team01.project.domain.calendardate.service

import com.team01.project.domain.calendardate.controller.dto.response.CalendarDateFetchResponse
import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.calendardate.repository.CalendarDateRepository
import com.team01.project.domain.musicrecord.service.MusicRecordService
import com.team01.project.domain.notification.event.NotificationRecordEvent
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.exception.CalendarDateAlreadyExistsException
import com.team01.project.global.exception.PermissionDeniedException
import com.team01.project.global.permission.CalendarPermission
import com.team01.project.global.permission.PermissionService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.util.*

@Service
@Transactional
class CalendarDateService(
    private val calendarDateRepository: CalendarDateRepository,
    private val userRepository: UserRepository,
    private val permissionService: PermissionService,
    private val eventPublisher: ApplicationEventPublisher,
    private val musicRecordService: MusicRecordService
) {
    /**
     * 특정 연도와 월에 해당하는 캘린더 리스트 조회
     */
    fun findAllByYearAndMonth(
        calendarOwnerId: String,
        loggedInUserId: String,
        yearMonth: YearMonth
    ): List<CalendarDate> {
        val start = yearMonth.atDay(1)
        val end = yearMonth.atEndOfMonth()

        val calendarOwner = userRepository.getById(calendarOwnerId)
        val loggedInUser = userRepository.getById(loggedInUserId)

        val calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser)

        if (CalendarPermission.NONE == calendarPermission) {
            throw PermissionDeniedException("403-10", "먼슬리 캘린더를 조회할 권한이 없습니다.")
        }

        return calendarDateRepository.findByUserAndDateBetween(calendarOwner, start, end)
    }

    /**
     * 캘린더 및 음악 기록 조회
     */
    fun findCalendarDateWithMusics(calendarDateId: Long, loggedInUserId: String): CalendarDateFetchResponse {
        val calendarDate = calendarDateRepository.findWithOwnerByIdOrThrow(calendarDateId)
        val calendarOwner = calendarDate.user
        val loggedInUser = userRepository.getById(loggedInUserId)

        val calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser)

        if (CalendarPermission.NONE == calendarPermission) {
            throw PermissionDeniedException("403-11", "캘린더를 조회할 권한이 없습니다.")
        }

        val musics = musicRecordService.findMusicsByCalendarDateId(calendarDateId)

        return CalendarDateFetchResponse.of(calendarDate, musics, calendarPermission)
    }

    /**
     * 캘린더 생성
     */
    fun create(userId: String, date: LocalDate, memo: String): CalendarDate {
        val user = userRepository.getById(userId)

        if (calendarDateRepository.existsByUserAndDate(user, date)) {
            throw CalendarDateAlreadyExistsException("409-10", "해당 날짜의 캘린더가 이미 존재합니다.")
        }

        // 🔥 이벤트 발행 (`NotificationScheduler`에서 감지할 수 있도록) 캘린더 생성 알림
        eventPublisher.publishEvent(NotificationRecordEvent(this, LocalTime.now(), user))

        val calendarDate = CalendarDate(
            user = user,
            date = date,
            memo = memo
        )

        return calendarDateRepository.save(calendarDate)
    }

    /**
     * 메모 수정
     */
    fun updateMemo(calendarDateId: Long, loggedInUserId: String, memo: String) {
        val calendarDate = calendarDateRepository.findWithOwnerByIdOrThrow(calendarDateId)
        val calendarOwner = calendarDate.user
        val loggedInUser = userRepository.getById(loggedInUserId)

        val calendarPermission = permissionService.checkPermission(calendarOwner, loggedInUser)

        if (CalendarPermission.EDIT != calendarPermission) {
            throw PermissionDeniedException("403-12", "캘린더를 수정할 권한이 없습니다.")
        }

        calendarDate.writeMemo(memo)
    }

    /**
     * 오늘 날짜 캘린더 조회
     */
    fun findByUserIdAndDate(userId: String, date: LocalDate): Optional<CalendarDate> {
        return calendarDateRepository.findByUserIdAndDate(userId, date)
    }
}
