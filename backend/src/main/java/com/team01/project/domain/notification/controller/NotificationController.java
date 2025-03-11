package com.team01.project.domain.notification.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.notification.dto.ModifyNotificationReqBody;
import com.team01.project.domain.notification.dto.NotificationDto;
import com.team01.project.domain.notification.dto.NotificationUpdateRequest;
import com.team01.project.domain.notification.entity.Notification;
import com.team01.project.domain.notification.service.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:3000")  // 클라이언트 URL을 지정
public class NotificationController {
	private final NotificationService notificationService;

	// 전체 알림 목록 조회 (알림 설정 페이지에서 보여줄 목록)
	@GetMapping
	public ResponseEntity<List<NotificationDto>> getNotifications() {
		return ResponseEntity.ok(notificationService.getAllNotifications()
				.stream()
				.map(NotificationDto::new)
				.toList());
	}

	// 특정 사용자의 알림 목록 조회
	@GetMapping("/{user-id}/lists")
	public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable(name = "user-id") String userId) {
		return ResponseEntity.ok(notificationService.getUserNotifications(userId)
				.stream()
				.map(NotificationDto::new)
				.toList());
	}

	// 알림 단건 조회
	@GetMapping("/{notification-id}")
	public ResponseEntity<NotificationDto> getUserNotification(
			@PathVariable(name = "notification-id") Long notificationId) {
		Notification notification = notificationService.getNotification(notificationId);
		return ResponseEntity.ok(new NotificationDto(notification));
	}


	// 알림 변경 (시간만 변경 가능)
	@PutMapping("/{notification-id}/modify")
	public ResponseEntity<String> modifyNotification(
			@PathVariable(name = "notification-id") Long notificationId,
			@RequestBody @Valid ModifyNotificationReqBody modifyNotificationReqBody) {
		notificationService.updateNotification(
				notificationId, modifyNotificationReqBody.notificationTime());

		return ResponseEntity.ok("Notification modified");
	}

	// 알림 설정 업데이트 (이메일, 푸시알림)
	@PutMapping("/update")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateNotifications(@RequestBody NotificationUpdateRequest request) {
		notificationService.updateNotifications(request.notifications());
	}
}
