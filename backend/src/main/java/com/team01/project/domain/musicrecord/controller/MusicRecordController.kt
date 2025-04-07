package com.team01.project.domain.musicrecord.controller

import com.team01.project.domain.musicrecord.controller.dto.MusicRecordDto
import com.team01.project.domain.musicrecord.service.MusicRecordService
import com.team01.project.global.dto.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Tag(name = "Recap", description = "통계 API")
@RestController
@RequestMapping("/recap")
class MusicRecordController(
    private val musicRecordService: MusicRecordService
) {
    /**
     * 통계 위해서 사용
     * 특정 연도와 월에 기록한 MusicRecord 리스트 조회
     */
    @Operation(summary = "전체 기록 조회", description = "사용자의 음악 기록 목록 조회")
    @GetMapping
    fun getMusicRecords(
        @AuthenticationPrincipal user: OAuth2User,
        @RequestParam("startDate")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate,
        @RequestParam("endDate")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate
    ): RsData<List<MusicRecordDto>> {
        // OAuth2User에서 사용자 ID를 가져온 후 조회
        val userId = user.name
        val records = musicRecordService.getMusicRecordsByUserAndDateRange(userId, startDate, endDate)
            .map{ MusicRecordDto.from(it) }

        return RsData(
            code = "200-1",
            msg = "사용자의 음악 기록 목록 조회가 완료되었습니다.",
            data = records
        )
    }
}
