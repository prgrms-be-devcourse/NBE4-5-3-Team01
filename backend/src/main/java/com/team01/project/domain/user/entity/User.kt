package com.team01.project.domain.user.entity

import com.team01.project.domain.notification.entity.Notification
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "user_tbl")
open class User(
    @Id
    @Column(name = "user_id")
    open var id: String = "",

    open var userPassword: String? = null,

    @Email
    open var email: String? = null,

    open var name: String? = null,

    open var originalName: String? = null,

    open var userIntro: String? = null,

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    open var image: String? = null,

    @Column(name = "birthday")
    open var birthDay: LocalDate? = null,

    @Column(name = "create_at")
    open var createdDate: LocalDateTime? = null,

    open var field: String? = null,

    @Column(name = "calendar_visibility", nullable = false)
    @Enumerated(EnumType.STRING)
    open var calendarVisibility: CalendarVisibility = CalendarVisibility.PUBLIC,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var refreshTokens: RefreshToken? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var notifications: MutableList<Notification> = mutableListOf()
) {

    fun updateCalendarVisibility(newCalendarVisibility: CalendarVisibility) {
        if (calendarVisibility != newCalendarVisibility) {
            calendarVisibility = newCalendarVisibility
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as User
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
