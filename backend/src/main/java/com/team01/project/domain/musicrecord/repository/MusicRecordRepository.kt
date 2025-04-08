package com.team01.project.domain.musicrecord.repository

import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.musicrecord.entity.MusicRecord
import com.team01.project.domain.musicrecord.entity.MusicRecordId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.Optional

interface MusicRecordRepository : JpaRepository<MusicRecord, MusicRecordId> {
    fun findByCalendarDate(calendarDate: CalendarDate): List<MusicRecord>

    fun findTopByCalendarDate(calendarDate: CalendarDate): Optional<MusicRecord>

    // 특정 사용자의 가장 최근 캘린더 날짜 ID 가져오기
    @Query("SELECT cd.id FROM CalendarDate cd WHERE cd.user.id = :userId ORDER BY cd.date DESC LIMIT 1")
    fun findRecentCalendarDateIdByUserId(@Param("userId") userId: String): Optional<Long>

    // 해당 캘린더 날짜에 있는 음악 목록 가져오기
    @Query("SELECT mr.music.id FROM MusicRecord mr WHERE mr.calendarDate.id = :calendarDateId")
    fun findMusicIdsByCalendarDateId(@Param("calendarDateId") calendarDateId: Long): List<String>

    @Query(
        """
        SELECT mr FROM MusicRecord mr
        JOIN mr.calendarDate cd
        WHERE cd.user.id = :userId   
        AND cd.date BETWEEN :startDate AND :endDate 
        """
    )
    fun findMusicRecordsByUserAndDateRange(
        @Param("userId") userId: String,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<MusicRecord>
}
