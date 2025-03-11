package com.team01.project.domain.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.notification.dto.NotificationListDto;
import com.team01.project.domain.notification.service.NotificationListService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notificationLists")
@CrossOrigin(origins = "http://localhost:3000")  // 클라이언트 URL을 지정
public class NotificationListController {

	private final NotificationListService notificationListService;

	// 사용자의 알림 목록 조회
	@GetMapping("/{user-id}")
	public ResponseEntity<List<NotificationListDto>> getUserNotifications(
			@PathVariable(name = "user-id") String userId) {
		return ResponseEntity.ok(notificationListService.getUserNotificationLists(userId)
				.stream()
				.map(NotificationListDto::new)
				.toList());
	}

	// 알림 읽음 처리
	@PutMapping("/{notificationList-id}")
	public ResponseEntity<String> markNotificationAsRead(
			@PathVariable(name = "notificationList-id") Long notificationListId) {
		notificationListService.markAsRead(notificationListId);
		return ResponseEntity.ok("Notification marked as read");
	}

	// 알림 리스트에서 알림 삭제
	@DeleteMapping("/{notificationList-id}")
	public ResponseEntity<String> deleteNotification(
			@PathVariable(name = "notificationList-id") Long notificationListId) {
		notificationListService.deleteNotification(notificationListId);
		return ResponseEntity.ok("Notification deleted");
	}
}
