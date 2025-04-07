package com.team01.project.domain.calendardate.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.user.entity.User;

public interface CalendarDateRepository extends JpaRepository<CalendarDate, Long> {

	List<CalendarDate> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

	boolean existsByUserAndDate(User user, LocalDate date);

	boolean existsByIdAndUser(Long calendarDateId, User user);

	@Query("SELECT c FROM CalendarDate c JOIN FETCH c.user WHERE c.id = :calendarDateId")
	Optional<CalendarDate> findWithOwnerById(@Param("calendarDateId") Long calendarDateId);

	default CalendarDate findByIdOrThrow(Long calendarDateId) {
		return findById(calendarDateId).orElseThrow(()
			-> new IllegalArgumentException("해당 ID의 캘린더 기록을 찾을 수 없습니다: " + calendarDateId));
	}

	default CalendarDate findWithOwnerByIdOrThrow(Long calendarDateId) {
		return findWithOwnerById(calendarDateId).orElseThrow(()
			-> new IllegalArgumentException("해당 ID의 캘린더 기록을 찾을 수 없습니다: " + calendarDateId));
	}

	Optional<CalendarDate> findByUserIdAndDate(String userId, LocalDate date);

}