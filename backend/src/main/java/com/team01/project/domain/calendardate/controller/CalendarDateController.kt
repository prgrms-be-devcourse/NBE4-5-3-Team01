package com.team01.project.domain.calendardate.controller

import com.team01.project.domain.calendardate.controller.dto.request.CalendarDateCreateRequest
import com.team01.project.domain.calendardate.controller.dto.request.CalendarDateMemoSaveRequest
import com.team01.project.domain.calendardate.controller.dto.request.CalendarDateMusicSaveRequest
import com.team01.project.domain.calendardate.controller.dto.response.CalendarDateCreateResponse
import com.team01.project.domain.calendardate.controller.dto.response.CalendarDateFetchResponse
import com.team01.project.domain.calendardate.controller.dto.response.MonthlyFetchResponse
import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.calendardate.service.CalendarDateService
import com.team01.project.domain.musicrecord.service.MusicRecordService
import com.team01.project.global.dto.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.YearMonth

@Tag(name = "Calendar", description = "캘린더 API")
@RestController
@RequestMapping("/calendar")
class CalendarDateController(
    private val calendarDateService: CalendarDateService,
    private val musicRecordService: MusicRecordService
) {
    /**
     * 먼슬리 캘린더 조회
     * @param year 연도
     * @param month 월
     * @param calendarOwnerId 캘린더 소유자 아이디
     * @param loggedInUser 현재 인증된 유저
     * @return 먼슬리 캘린더
     */
    @Operation(
        summary = "먼슬리 캘린더 조회 api",
        description = "현재 인증된 유저 또는 헤더의 Calendar-Owner-Id와 동일한 아이디를 갖는 유저의 먼슬리 캘린더 조회"
    )
    @GetMapping(params = ["year", "month"])
    fun fetchMonthlyCalendar(
        @RequestParam year: Int,
        @RequestParam month: Int,
        @RequestHeader(name = "Calendar-Owner-Id", required = false) calendarOwnerId: String?,
        @AuthenticationPrincipal loggedInUser: OAuth2User
    ): RsData<MonthlyFetchResponse> {
        val loggedInUserId = loggedInUser.name
        val yearMonth = YearMonth.of(year, month)

        val monthly = mapToMonthly(
            calendarDateService.findAllByYearAndMonth(
                calendarOwnerId ?: loggedInUserId,
                loggedInUserId,
                yearMonth
            )
        )

        return RsData(
            "200-10",
            "먼슬리 캘린더 조회에 성공했습니다.",
            MonthlyFetchResponse(monthly)
        )
    }

    /**
     * 캘린더 조회
     * @param calendarDateId 캘린더 아이디
     * @param loggedInUser 현재 인증된 유저
     * @return 캘린더
     */
    @Operation(summary = "캘린더 조회 api", description = "캘린더 아이디에 해당하는 캘린더 조회")
    @GetMapping("/{calendar-date-id}")
    fun fetchCalendarDate(
        @PathVariable(name = "calendar-date-id") calendarDateId: Long,
        @AuthenticationPrincipal loggedInUser: OAuth2User
    ): RsData<CalendarDateFetchResponse> {
        val loggedInUserId = loggedInUser.name

        // CalendarDate와 함께 CalendarDate에 기록된 Music 리스트 조회
        val responseDto = calendarDateService.findCalendarDateWithMusics(calendarDateId, loggedInUserId)

        return RsData(
            "200-11",
            "캘린더 조회에 성공했습니다.",
            responseDto
        )
    }

    /**
     * 캘린더 생성
     * @param year 연도
     * @param month 월
     * @param day 일
     * @param request 메모, 음악 아이디 리스트
     * @param loggedInUser 현재 인증된 유저
     * @return 생성된 캘린더 아이디
     */
    @Operation(summary = "캘린더 생성 api", description = "현재 인증된 유저의 캘린더 생성")
    @PostMapping(params = ["year", "month", "day"])
    fun createCalendarDate(
        @RequestParam year: Int,
        @RequestParam month: Int,
        @RequestParam day: Int,
        @RequestBody
        @Valid
        request: CalendarDateCreateRequest,
        @AuthenticationPrincipal loggedInUser: OAuth2User
    ): RsData<CalendarDateCreateResponse> {
        val loggedInUserId = loggedInUser.name
        val date = LocalDate.of(year, month, day)

        // 캘린더 생성
        val calendarDate = calendarDateService.create(loggedInUserId, date, request.memo)

        // 음악 기록 저장
        musicRecordService.createMusicRecords(calendarDate.id!!, request.musicIds)

        return RsData(
            "201-10",
            "캘린더 생성에 성공했습니다.",
            CalendarDateCreateResponse(calendarDate.id!!)
        )
    }

    /**
     * 음악 기록 수정
     * @param calendarDateId 캘린더 아이디
     * @param request 음악 아이디 리스트
     * @param loggedInUser 현재 인증된 유저
     */
    @Operation(summary = "음악 기록 수정 api", description = "현재 인증된 유저의 특정 캘린더 날짜에 대한 음악 기록 수정")
    @PutMapping("/{calendar-date-id}/music")
    fun saveMusicToCalendarDate(
        @PathVariable(name = "calendar-date-id") calendarDateId: Long,
        @RequestBody request: CalendarDateMusicSaveRequest,
        @AuthenticationPrincipal loggedInUser: OAuth2User
    ): RsData<Void> {
        val loggedInUserId = loggedInUser.name
        musicRecordService.updateMusicRecords(calendarDateId, loggedInUserId, request.musicIds)

        return RsData(
            "200-12",
            "음악 기록 수정에 성공했습니다."
        )
    }

    /**
     * 메모 기록 수정
     * @param calendarDateId 캘린더 아이디
     * @param request 새로운 메모
     * @param loggedInUser 현재 인증된 유저
     */
    @Operation(summary = "메모 기록 수정 api", description = "현재 인증된 유저의 특정 캘린더 날짜에 대한 메모 기록 수정")
    @PatchMapping("/{calendar-date-id}/memo")
    fun writeMemoToCalendarDate(
        @PathVariable(name = "calendar-date-id") calendarDateId: Long,
        @RequestBody
        @Valid
        request: CalendarDateMemoSaveRequest,
        @AuthenticationPrincipal loggedInUser: OAuth2User
    ): RsData<Void> {
        val loggedInUserId = loggedInUser.name
        calendarDateService.updateMemo(calendarDateId, loggedInUserId, request.memo)

        return RsData(
            "200-13",
            "메모 기록 수정에 성공했습니다."
        )
    }

    /**
     * 오늘 날짜의 캘린더 조회
     * @param loggedInUser 현재 인증된 유저
     * @return 캘린더 아이디 또는 오늘 날짜 정보
     */
    @Operation(summary = "오늘 날짜의 캘린더 조회", description = "오늘 날짜의 캘린더 기록이 존재하는지 확인하여 아이디 또는 날짜 반환")
    @GetMapping("/today")
    fun fetchTodayCalendarIfExists(@AuthenticationPrincipal loggedInUser: OAuth2User): RsData<*> {
        val loggedInUserId = loggedInUser.name
        val today = LocalDate.now()

        val todayRecord = calendarDateService.findByUserIdAndDate(loggedInUserId, today)

        if (todayRecord.isPresent) {
            return RsData("200-1", "오늘 기록이 존재합니다.", todayRecord.get().id)
        } else {
            val todayInfo = mapOf(
                "year" to today.year,
                "month" to today.monthValue,
                "day" to today.dayOfMonth
            )
            return RsData(
                "200-2",
                "오늘 기록이 없습니다.",
                todayInfo
            )
        }
    }

    private fun mapToMonthly(calendarDates: List<CalendarDate>): List<MonthlyFetchResponse.SingleCalendarDate> {
        return calendarDates.map { mapToSingleCalendarDate(it) }
    }

    private fun mapToSingleCalendarDate(calendarDate: CalendarDate): MonthlyFetchResponse.SingleCalendarDate {
        val musicRecord = musicRecordService.findOneByCalendarDateId(calendarDate.id!!)
        return musicRecord
            .map { MonthlyFetchResponse.SingleCalendarDate.of(calendarDate, it.music) }
            .orElseGet { MonthlyFetchResponse.SingleCalendarDate.from(calendarDate) }
    }
}
