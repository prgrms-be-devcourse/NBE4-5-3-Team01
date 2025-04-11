package com.team01.project.calendardate.fixture

import com.team01.project.domain.calendardate.entity.CalendarDate
import com.team01.project.domain.user.entity.User
import com.team01.project.user.entity.UserFixture
import java.time.LocalDate

object CalendarDateFixture {
    private val defaultUser = UserFixture.유저("user")

    fun calendarDate(
        id: Long = 1L,
        user: User = defaultUser,
        date: LocalDate = LocalDate.now(),
        memo: String = "memo"
    ): CalendarDate {
        return CalendarDate(
            id = id,
            user = user,
            date = date,
            memo = memo
        )
    }
}
