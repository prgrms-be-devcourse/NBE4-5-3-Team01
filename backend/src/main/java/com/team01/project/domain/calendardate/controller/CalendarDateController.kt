package com.team01.project.domain.calendardate.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.calendardate.controller.dto.request.CalendarDateCreateRequest;
import com.team01.project.domain.calendardate.controller.dto.request.CalendarDateMemoSaveRequest;
import com.team01.project.domain.calendardate.controller.dto.request.CalendarDateMusicSaveRequest;
import com.team01.project.domain.calendardate.controller.dto.response.CalendarDateCreateResponse;
import com.team01.project.domain.calendardate.controller.dto.response.CalendarDateFetchResponse;
import com.team01.project.domain.calendardate.controller.dto.response.MonthlyFetchResponse;
import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.calendardate.service.CalendarDateService;
import com.team01.project.domain.musicrecord.entity.MusicRecord;
import com.team01.project.domain.musicrecord.service.MusicRecordService;
import com.team01.project.global.dto.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Calendar", description = "캘린더 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarDateController {

	private final CalendarDateService calendarDateService;
	private final MusicRecordService musicRecordService;

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
	@GetMapping(params = {"year", "month"})
	public RsData<MonthlyFetchResponse> fetchMonthlyCalendar(
		@RequestParam int year,
		@RequestParam int month,
		@RequestHeader(name = "Calendar-Owner-Id", required = false) String calendarOwnerId,
		@AuthenticationPrincipal OAuth2User loggedInUser
	) {
		String loggedInUserId = loggedInUser.getName();
		YearMonth yearMonth = YearMonth.of(year, month);

		List<MonthlyFetchResponse.SingleCalendarDate> monthly = mapToMonthly(
			calendarDateService.findAllByYearAndMonth(
				calendarOwnerId == null ? loggedInUserId : calendarOwnerId,
				loggedInUserId,
				yearMonth)
		);

		return new RsData<>(
			"200-10",
			"먼슬리 캘린더 조회에 성공했습니다.",
			new MonthlyFetchResponse(monthly)
		);
	}

	/**
	 * 캘린더 조회
	 * @param calendarDateId 캘린더 아이디
	 * @param loggedInUser 현재 인증된 유저
	 * @return 캘린더
	 */
	@Operation(summary = "캘린더 조회 api", description = "캘린더 아이디에 해당하는 캘린더 조회")
	@GetMapping("/{calendar-date-id}")
	public RsData<CalendarDateFetchResponse> fetchCalendarDate(
		@PathVariable(name = "calendar-date-id") Long calendarDateId,
		@AuthenticationPrincipal OAuth2User loggedInUser
	) {
		String loggedInUserId = loggedInUser.getName();

		// CalendarDate와 함께 CalendarDate에 기록된 Music 리스트 조회
		CalendarDateFetchResponse responseDto =
			calendarDateService.findCalendarDateWithMusics(calendarDateId, loggedInUserId);

		return new RsData<>(
			"200-11",
			"캘린더 조회에 성공했습니다.",
			responseDto
		);
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
	@PostMapping(params = {"year", "month", "day"})
	public RsData<CalendarDateCreateResponse> createCalendarDate(
		@RequestParam int year,
		@RequestParam int month,
		@RequestParam int day,
		@RequestBody CalendarDateCreateRequest request,
		@AuthenticationPrincipal OAuth2User loggedInUser
	) {
		String loggedInUserId = loggedInUser.getName();
		LocalDate date = LocalDate.of(year, month, day);

		// 캘린더 생성
		CalendarDate calendarDate = calendarDateService.create(loggedInUserId, date, request.memo());

		Long calendarDateId = calendarDate.getId();

		// 음악 기록 저장
		musicRecordService.createMusicRecords(calendarDateId, request.musicIds());

		return new RsData<>(
			"201-10",
			"캘린더 생성에 성공했습니다",
			new CalendarDateCreateResponse(calendarDateId)
		);
	}

	/**
	 * 음악 기록 수정
	 * @param calendarDateId 캘린더 아이디
	 * @param request 음악 아이디 리스트
	 * @param loggedInUser 현재 인증된 유저
	 */
	@Operation(summary = "음악 기록 수정 api", description = "현재 인증된 유저의 특정 캘린더 날짜에 대한 음악 기록 수정")
	@PutMapping("/{calendar-date-id}/music")
	public RsData<Void> saveMusicToCalendarDate(
		@PathVariable(name = "calendar-date-id") Long calendarDateId,
		@RequestBody CalendarDateMusicSaveRequest request,
		@AuthenticationPrincipal OAuth2User loggedInUser
	) {
		String loggedInUserId = loggedInUser.getName();
		musicRecordService.updateMusicRecords(calendarDateId, loggedInUserId, request.musicIds());

		return new RsData<>(
			"200-12",
			"음악 기록 수정에 성공했습니다."
		);
	}

	/**
	 * 메모 기록 수정
	 * @param calendarDateId 캘린더 아이디
	 * @param request 새로운 메모
	 * @param loggedInUser 현재 인증된 유저
	 */
	@Operation(summary = "메모 기록 수정 api", description = "현재 인증된 유저의 특정 캘린더 날짜에 대한 메모 기록 수정")
	@PatchMapping("/{calendar-date-id}/memo")
	public RsData<Void> writeMemoToCalendarDate(
		@PathVariable(name = "calendar-date-id") Long calendarDateId,
		@RequestBody CalendarDateMemoSaveRequest request,
		@AuthenticationPrincipal OAuth2User loggedInUser
	) {
		String loggedInUserId = loggedInUser.getName();
		calendarDateService.updateMemo(calendarDateId, loggedInUserId, request.memo());

		return new RsData<>(
			"200-13",
			"메모 기록 수정에 성공했습니다."
		);
	}

	private List<MonthlyFetchResponse.SingleCalendarDate> mapToMonthly(List<CalendarDate> calendarDates) {
		return calendarDates.stream().map(this::mapToSingleCalendarDate).toList();
	}

	private MonthlyFetchResponse.SingleCalendarDate mapToSingleCalendarDate(CalendarDate calendarDate) {
		Optional<MusicRecord> optionalMusicRecord = musicRecordService.findOneByCalendarDateId(calendarDate.getId());

		return optionalMusicRecord.map(
				musicRecord -> MonthlyFetchResponse.SingleCalendarDate.of(calendarDate, musicRecord.getMusic()))
			.orElseGet(() -> MonthlyFetchResponse.SingleCalendarDate.from(calendarDate));
	}

	/**
	 * 오늘 날짜의 캘린더 조회
	 * @param loggedInUser 현재 인증된 유저
	 * @return 캘린더 아이디 또는 오늘 날짜 정보
	 */
	@Operation(summary = "오늘 날짜의 캘린더 조회", description = "오늘 날짜의 캘린더 기록이 존재하는지 확인하여 아이디 또는 날짜 반환")
	@GetMapping("/today")
	public RsData<?> checkToday(@AuthenticationPrincipal OAuth2User loggedInUser) {
		String loggedInUserId = loggedInUser.getName();
		LocalDate today = LocalDate.now();

		Optional<CalendarDate> todayRecord = calendarDateService.findByUserIdAndDate(loggedInUserId, today);

		if (todayRecord.isPresent()) {
			return new RsData<>("200-1", "오늘 기록이 존재합니다.", todayRecord.get().getId());
		} else {
			Map<String, Integer> todayInfo = Map.of(
				"year", today.getYear(),
				"month", today.getMonthValue(),
				"day", today.getDayOfMonth()
			);
			return new RsData<>("200-2", "오늘 기록이 없습니다.", todayInfo);
		}
	}
}