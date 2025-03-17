package com.team01.project.domain.calendardate.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.calendardate.controller.dto.request.CalendarDateCreateRequest;
import com.team01.project.domain.calendardate.controller.dto.request.CalendarDateMemoSaveRequest;
import com.team01.project.domain.calendardate.controller.dto.request.CalendarDateMusicSaveRequest;
import com.team01.project.domain.calendardate.controller.dto.response.CalendarDateCreateResponse;
import com.team01.project.domain.calendardate.controller.dto.response.CalendarDateFetchResponse;
import com.team01.project.domain.calendardate.controller.dto.response.MonthlyFetchResponse;
import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.calendardate.service.CalendarDateService;
import com.team01.project.domain.music.entity.Music;
import com.team01.project.domain.musicrecord.entity.MusicRecord;
import com.team01.project.domain.musicrecord.service.MusicRecordService;

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
	 * @param ownerId 캘린더 소유자 아이디
	 * @param loggedInUser 현재 인증된 유저
	 * @return 먼슬리 캘린더
	 */
	@Operation(summary = "먼슬리 캘린더 조회 api", description = "현재 로그인 하고 있는 유저 또는 헤더의 Calendar-Own"
		+ "er-Id와 동일한 아이디를 갖는 유저의 먼슬리 캘린더 조회")
	@GetMapping(params = {"year", "month"})
	@ResponseStatus(HttpStatus.OK)
	public MonthlyFetchResponse fetchMonthlyCalendar(@RequestParam int year, @RequestParam int month,
		@RequestHeader(name = "Calendar-Owner-Id", required = false) String ownerId,
		@AuthenticationPrincipal OAuth2User loggedInUser) {
		String loggedInUserId = loggedInUser.getName();
		YearMonth yearMonth = YearMonth.of(year, month);

		List<MonthlyFetchResponse.SingleCalendarDate> monthly = mapToMonthly(
			calendarDateService.findAllByYearAndMonth(ownerId, loggedInUserId, yearMonth));

		return new MonthlyFetchResponse(monthly);
	}

	/**
	 * 캘린더 조회
	 * @param calendarDateId 캘린더 아이디
	 * @param loggedInUser 현재 인증된 유저
	 * @return 캘린더
	 */
	@Operation(summary = "캘린더 조회 api", description = "현재 로그인 하고 있는 유저의 캘린더 조회")
	@GetMapping("/{calendar-date-id}")
	@ResponseStatus(HttpStatus.OK)
	public CalendarDateFetchResponse fetchCalendarDate(@PathVariable(name = "calendar-date-id") Long calendarDateId,
		@AuthenticationPrincipal OAuth2User loggedInUser) {
		String loggedInUserId = loggedInUser.getName();

		// CalendarDate 조회
		CalendarDate calendarDate = calendarDateService.findById(calendarDateId, loggedInUserId);

		// CalendarDate와 연관된 MusicRecord를 이용해 Music 리스트 조회
		List<Music> musics = musicRecordService.findMusicsByCalendarDateId(calendarDateId, loggedInUserId);

		return CalendarDateFetchResponse.of(calendarDate, musics);
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
	@Operation(summary = "캘린더 생성 api", description = "현재 로그인 하고 있는 유저의 캘린더 생성")
	@PostMapping(params = {"year", "month", "day"})
	@ResponseStatus(HttpStatus.CREATED)
	public CalendarDateCreateResponse createCalendarDate(@RequestParam int year, @RequestParam int month,
		@RequestParam int day, @RequestBody CalendarDateCreateRequest request,
		@AuthenticationPrincipal OAuth2User loggedInUser) {
		String loggedInUserId = loggedInUser.getName();
		LocalDate date = LocalDate.of(year, month, day);

		// 캘린더 생성
		CalendarDate calendarDate = calendarDateService.create(loggedInUserId, date, request.memo());

		Long calendarDateId = calendarDate.getId();

		// 음악 기록 저장
		musicRecordService.updateMusicRecords(calendarDateId, loggedInUserId, request.musicIds());

		return new CalendarDateCreateResponse(calendarDateId);
	}

	/**
	 * 음악 기록 저장
	 * @param calendarDateId 캘린더 아이디
	 * @param request 음악 아이디 리스트
	 * @param loggedInUser 현재 인증된 유저
	 */
	@Operation(summary = "작성된 음악 기록 수정 api", description = "현재 로그인 하고 있는 유저의 음악 기록 수정할때만 사용")
	@PostMapping("/{calendar-date-id}/music")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void saveMusicToCalendarDate(@PathVariable(name = "calendar-date-id") Long calendarDateId,
		@RequestBody CalendarDateMusicSaveRequest request, @AuthenticationPrincipal OAuth2User loggedInUser) {
		String loggedInUserId = loggedInUser.getName();
		musicRecordService.updateMusicRecords(calendarDateId, loggedInUserId, request.musicIds());
	}

	/**
	 * 메모 작성
	 * @param calendarDateId 캘린더 아이디
	 * @param request 새로운 메모
	 * @param loggedInUser 현재 인증된 유저
	 */
	@Operation(summary = "작성된 메모 수정 api", description = "현재 로그인 하고 있는 유저의 메모 작성 수정할때만 사용")
	@PostMapping("/{calendar-date-id}/memo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void writeMemoToCalendarDate(@PathVariable(name = "calendar-date-id") Long calendarDateId,
		@RequestBody CalendarDateMemoSaveRequest request, @AuthenticationPrincipal OAuth2User loggedInUser) {
		String loggedInUserId = loggedInUser.getName();
		calendarDateService.writeMemo(calendarDateId, loggedInUserId, request.memo());
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

}