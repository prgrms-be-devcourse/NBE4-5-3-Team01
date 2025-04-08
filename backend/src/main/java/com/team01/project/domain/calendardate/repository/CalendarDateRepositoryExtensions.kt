package com.team01.project.domain.calendardate.repository

import com.team01.project.domain.calendardate.entity.CalendarDate

fun CalendarDateRepository.findByIdOrThrow(id: Long): CalendarDate =
    this.findById(id).orElseThrow {
        IllegalArgumentException("해당 ID의 캘린더 기록을 찾을 수 없습니다: $id")
    }

fun CalendarDateRepository.findWithOwnerByIdOrThrow(id: Long): CalendarDate =
    this.findWithOwnerById(id).orElseThrow {
        IllegalArgumentException("해당 ID의 캘린더 기록을 찾을 수 없습니다: $id")
    }
