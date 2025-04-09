package com.team01.project.domain.calendardate.entity

val CalendarDate.idOrThrow: Long
    get() = requireNotNull(id) { "CalendarDate ID는 null일 수 없습니다." }
