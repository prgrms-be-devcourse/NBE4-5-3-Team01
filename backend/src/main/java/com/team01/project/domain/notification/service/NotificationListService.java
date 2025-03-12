package com.team01.project.domain.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
	public void markAsRead(Long notificationListId, String userId) {
		NotificationList notificationList = notificationListRepository.findById(notificationListId)
				.orElseThrow(() ->
						new IllegalArgumentException("Notification not found with ID: " + notificationListId));

		// 현재 로그인한 사용자의 알림인지 검증
		if (!notificationList.getUser().getId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"You do not have permission to update this notification.");
		}

		notificationList.markAsRead();
		notificationListRepository.save(notificationList);
	}

	@Transactional
	public void deleteNotification(Long notificationListId, String userId) {
		NotificationList notificationList = notificationListRepository.findById(notificationListId)
				.orElseThrow(() ->
						new IllegalArgumentException("Notification not found with ID: " + notificationListId));

		// 현재 로그인한 사용자의 알림인지 검증
		if (!notificationList.getUser().getId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"You do not have permission to delete this notification.");
		}

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
