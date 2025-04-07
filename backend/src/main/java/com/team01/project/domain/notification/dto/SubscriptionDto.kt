package com.team01.project.domain.notification.dto

data class SubscriptionDto(
    val endpoint: String,
    val keys: Keys
) {
    data class Keys(
        val p256dh: String,
        val auth: String
    )
}
