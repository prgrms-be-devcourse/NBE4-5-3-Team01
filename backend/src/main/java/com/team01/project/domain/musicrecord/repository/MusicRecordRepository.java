package com.team01.project.domain.musicrecord.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.musicrecord.entity.MusicRecord;
import com.team01.project.domain.musicrecord.entity.MusicRecordId;

public interface MusicRecordRepository extends JpaRepository<MusicRecord, MusicRecordId> {

	List<MusicRecord> findByCalendarDate(CalendarDate calendarDate);

	Optional<MusicRecord> findTopByCalendarDate(CalendarDate calendarDate);

	// 특정 사용자의 가장 최근 캘린더 날짜 ID 가져오기
	@Query("SELECT cd.id FROM CalendarDate cd WHERE cd.user.id = :userId ORDER BY cd.date DESC LIMIT 1")
	Optional<Long> findRecentCalendarDateIdByUserId(@Param("userId") String userId);

	// 해당 캘린더 날짜에 있는 음악 목록 가져오기
	@Query("SELECT mr.music.id FROM MusicRecord mr WHERE mr.calendarDate.id = :calendarDateId")
	List<String> findMusicIdsByCalendarDateId(@Param("calendarDateId") Long calendarDateId);
}