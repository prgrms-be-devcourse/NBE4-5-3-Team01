package com.team01.project.domain.musicrecord.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.musicrecord.controller.dto.MusicRecordDto;
import com.team01.project.domain.musicrecord.service.MusicRecordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Recap", description = "통계 API")
@RestController
@RequestMapping("/recap")
@RequiredArgsConstructor
public class MusicRecordController {

	private final MusicRecordService musicRecordService;

	/**
	 * 통계 위해서 사용
	 * 특정 연도와 월에 기록한 MusicRecord 리스트 조회
	 */
	@Operation(summary = "전체 기록 조회", description = "사용자의 음악 기록 목록 조회")
	@GetMapping
	public ResponseEntity<List<MusicRecordDto>> getMusicRecords(
			@AuthenticationPrincipal OAuth2User user,
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		// OAuth2User에서 사용자 ID를 가져온 후 조회
		String userId = user.getName();
		List<MusicRecordDto> records =
				musicRecordService.getMusicRecordsByUserAndDateRange(userId, startDate, endDate).stream()
						.map(MusicRecordDto::from)
						.collect(Collectors.toList());
		return ResponseEntity.ok(records);
	}
}
