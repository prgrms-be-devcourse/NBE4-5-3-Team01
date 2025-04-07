package com.team01.project.domain.notification.entity

import com.team01.project.domain.user.entity.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
data class Subscription(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "USER_ID", nullable = false)
    var user: User,

    var endpoint: String,

    var p256dh: String,

    var auth: String

) {

    fun update(endpoint: String, p256dh: String, auth: String) {
        this.endpoint = endpoint
        this.p256dh = p256dh
        this.auth = auth
    }

    fun updateWithUser(user: User, endpoint: String, p256dh: String, auth: String) {
        this.user = user
        this.endpoint = endpoint
        this.p256dh = p256dh
        this.auth = auth
    }

    companion object {
        fun builder() = SubscriptionBuilder()
    }

    class SubscriptionBuilder {
        private var id: Long? = null
        private lateinit var user: User
        private lateinit var endpoint: String
        private lateinit var p256dh: String
        private lateinit var auth: String

        fun id(id: Long?) = apply { this.id = id }
        fun user(user: User) = apply { this.user = user }
        fun endpoint(endpoint: String) = apply { this.endpoint = endpoint }
        fun p256dh(p256dh: String) = apply { this.p256dh = p256dh }
        fun auth(auth: String) = apply { this.auth = auth }

        fun build(): Subscription {
            return Subscription(
                id = id,
                user = user,
                endpoint = endpoint,
                p256dh = p256dh,
                auth = auth
            )
        }
    }
}
