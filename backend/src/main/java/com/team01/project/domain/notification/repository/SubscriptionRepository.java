package com.team01.project.domain.notification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team01.project.domain.notification.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	Optional<Subscription> findByUserId(String userId);

	Optional<Subscription> findByEndpoint(String endpoint);
}
