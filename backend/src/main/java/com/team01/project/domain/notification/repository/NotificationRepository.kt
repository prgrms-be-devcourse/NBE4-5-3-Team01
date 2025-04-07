package com.team01.project.domain.notification.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team01.project.domain.notification.entity.Notification;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByUserId(String userId);

	@EntityGraph(attributePaths = {"user"})
	List<Notification> findByNotificationTime(LocalTime notificationTime);

	@Query("SELECT n FROM Notification n WHERE n.notificationTime >= :now AND n.notificationTime < :plusMinutes")
	List<Notification> findNotificationsBetween(@Param("now") LocalTime now,
												@Param("plusMinutes") LocalTime plusMinutes);

	@Query("SELECT DISTINCT n.notificationTime FROM Notification n "
			+ "WHERE n.notificationTime >= :start AND n.notificationTime < :end ORDER BY n.notificationTime ASC")
	List<LocalTime> findDistinctNotificationTimeBetween(
			@Param("start") LocalTime start, @Param("end") LocalTime end);

}
