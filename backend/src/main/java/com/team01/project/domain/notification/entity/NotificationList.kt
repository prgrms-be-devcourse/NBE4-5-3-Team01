package com.team01.project.domain.notification.entity

import com.team01.project.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class NotificationList(

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

    @Column(nullable = false)
    val notificationTime: LocalDateTime,

    var isRead: Boolean = false

) {
    fun markAsRead() {
        this.isRead = true
    }

    companion object {
        fun builder() = NotificationListBuilder()
    }

    class NotificationListBuilder {
        private var id: Long? = null
        private lateinit var user: User
        private lateinit var title: String
        private lateinit var message: String
        private lateinit var notificationTime: LocalDateTime
        private var isRead: Boolean = false

        fun id(id: Long?) = apply { this.id = id }
        fun user(user: User) = apply { this.user = user }
        fun title(title: String) = apply { this.title = title }
        fun message(message: String) = apply { this.message = message }
        fun notificationTime(notificationTime: LocalDateTime) = apply { this.notificationTime = notificationTime }
        fun isRead(isRead: Boolean) = apply { this.isRead = isRead }

        fun build(): NotificationList {
            return NotificationList(
                id = id,
                user = user,
                title = title,
                message = message,
                notificationTime = notificationTime,
                isRead = isRead
            )
        }
    }
}
