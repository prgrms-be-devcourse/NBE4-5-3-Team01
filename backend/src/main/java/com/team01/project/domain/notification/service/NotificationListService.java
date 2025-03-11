package com.team01.project.domain.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.notification.entity.NotificationList;
import com.team01.project.domain.notification.repository.NotificationListRepository;
import com.team01.project.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationListService {

	private final NotificationListRepository notificationListRepository;

	@Transactional(readOnly = true)
	public List<NotificationList> getUserNotificationLists(String userId) {
		return notificationListRepository.findByUserId(userId);
	}

	@Transactional
	public void markAsRead(Long notificationListId) {
		NotificationList notificationList = notificationListRepository.findById(notificationListId)
				.orElseThrow(() ->
						new IllegalArgumentException("Notification not found with ID: " + notificationListId));

		notificationList.markAsRead();
		notificationListRepository.save(notificationList);
	}

	@Transactional
	public void deleteNotification(Long notificationListId) {
		notificationListRepository.deleteById(notificationListId);
	}

	@Transactional
	public void addNotification(User user, String title, String message, LocalDateTime notificationTime) {
		notificationListRepository.save(NotificationList.builder()
				.user(user)
				.title(title)
				.message(message)
				.notificationTime(notificationTime)
				.build());
	}
}
