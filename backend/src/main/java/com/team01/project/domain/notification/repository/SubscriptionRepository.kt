package com.team01.project.domain.notification.repository

import com.team01.project.domain.notification.entity.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface SubscriptionRepository : JpaRepository<Subscription, Long> {
    fun findByUserId(userId: String): Optional<Subscription>

    fun findByEndpoint(endpoint: String): Optional<Subscription>
}
