package com.team01.project.domain.calendardate.repository

import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.Optional

interface CalendarDateRepository : JpaRepository<CalendarDate, Long> {
    fun findByUserAndDateBetween(user: User, start: LocalDate, end: LocalDate): List<CalendarDate>

    fun existsByUserAndDate(user: User, date: LocalDate): Boolean

    @Query("SELECT c FROM CalendarDate c JOIN FETCH c.user WHERE c.id = :calendarDateId")
    fun findWithOwnerById(@Param("calendarDateId") calendarDateId: Long): Optional<CalendarDate>

    fun findByUserIdAndDate(userId: String, date: LocalDate): Optional<CalendarDate>
}
