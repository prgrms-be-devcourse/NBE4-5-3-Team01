package com.team01.project.domain.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team01.project.domain.notification.entity.NotificationList;


public interface NotificationListRepository extends JpaRepository<NotificationList, Long> {
	List<NotificationList> findByUserId(String userId);

	List<NotificationList> findByUserIdAndIsReadFalse(String userId);
}
