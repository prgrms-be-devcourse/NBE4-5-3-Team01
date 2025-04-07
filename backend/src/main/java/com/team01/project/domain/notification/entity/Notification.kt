package com.team01.project.domain.notification.entity

import com.team01.project.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalTime

@Entity
data class Notification(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val message: String,

    var notificationTime: LocalTime? = null,

    var isEmailEnabled: Boolean = true,

    var isPushEnabled: Boolean = true

) {

    fun updateNotificationTime(notificationTime: LocalTime?) {
        this.notificationTime = notificationTime
    }

    fun updateNotificationSettings(emailEnabled: Boolean?, pushEnabled: Boolean?) {
        emailEnabled?.let { this.isEmailEnabled = it }
        pushEnabled?.let { this.isPushEnabled = it }
    }

    companion object {
        fun builder() = NotificationBuilder()
    }

    class NotificationBuilder {
        private var id: Long? = null
        private lateinit var user: User
        private lateinit var title: String
        private lateinit var message: String
        private var notificationTime: LocalTime? = null
        private var isEmailEnabled: Boolean = true
        private var isPushEnabled: Boolean = true

        fun id(id: Long?) = apply { this.id = id }
        fun user(user: User) = apply { this.user = user }
        fun title(title: String) = apply { this.title = title }
        fun message(message: String) = apply { this.message = message }
        fun notificationTime(notificationTime: LocalTime?) = apply { this.notificationTime = notificationTime }
        fun isEmailEnabled(isEmailEnabled: Boolean) = apply { this.isEmailEnabled = isEmailEnabled }
        fun isPushEnabled(isPushEnabled: Boolean) = apply { this.isPushEnabled = isPushEnabled }

        fun build(): Notification {
            return Notification(
                id = id,
                user = user,
                title = title,
                message = message,
                notificationTime = notificationTime,
                isEmailEnabled = isEmailEnabled,
                isPushEnabled = isPushEnabled
            )
        }
    }
}
