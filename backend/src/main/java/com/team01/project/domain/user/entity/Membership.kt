package com.team01.project.domain.user.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import java.time.LocalDate

@Entity
data class Membership(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var grade: String = "basic", // 예: BASIC, PREMIUM
    var count: Int = 0,

    var startDate: LocalDate? = null,
    var endDate: LocalDate? = null,

    var autoRenew: Boolean = false,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User
) {
    companion object {
        fun default(user: User): Membership {
            return Membership(user = user)
        }
    }
}