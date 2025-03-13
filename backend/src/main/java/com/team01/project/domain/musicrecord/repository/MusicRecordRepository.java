package com.team01.project.domain.musicrecord.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.musicrecord.entity.MusicRecord;
import com.team01.project.domain.musicrecord.entity.MusicRecordId;

public interface MusicRecordRepository extends JpaRepository<MusicRecord, MusicRecordId> {

	List<MusicRecord> findByCalendarDate(CalendarDate calendarDate);

	Optional<MusicRecord> findTopByCalendarDate(CalendarDate calendarDate);

}