package com.team01.project.domain.calendardate.entity

import com.team01.project.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class CalendarDate(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(columnDefinition = "TEXT", nullable = false)
    var memo: String = ""

) {
    fun writeMemo(memo: String) {
        this.memo = memo
    }
}