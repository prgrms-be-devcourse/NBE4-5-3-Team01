package com.team01.project.domain.notification.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.notification.dto.ModifyNotificationReqBody;
import com.team01.project.domain.notification.dto.NotificationDto;
import com.team01.project.domain.notification.dto.NotificationUpdateDto;
import com.team01.project.domain.notification.entity.Notification;
import com.team01.project.domain.notification.service.NotificationService;
import com.team01.project.global.dto.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notification", description = "알림 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {
	private final NotificationService notificationService;

	// 전체 알림 목록 조회
	@Operation(summary = "전체 알림 조회", description = "모든 사용자의 알림 목록 조회")
	@GetMapping
	public RsData<List<NotificationDto>> getNotifications() {

		return new RsData<>(
				"200-1",
				"전체 알림 목록 조회가 완료되었습니다.",
				notificationService.getAllNotifications()
						.stream()
						.map(NotificationDto::new)
						.toList()
		);
	}

	// 사용자의 알림 목록 조회 (알림 설정 페이지에서 보여줄 목록)
	@Operation(summary = "사용자의 알림 조회", description = "한 사용자의 알림 목록 조회")
	@GetMapping("/lists")
	public RsData<List<NotificationDto>> getUserNotifications(@AuthenticationPrincipal OAuth2User user) {
		String userId = user.getName();
		return new RsData<>(
				"200-1",
				"사용자의 알림 목록 조회가 완료되었습니다.",
				notificationService.getUserNotifications(userId)
						.stream()
						.map(NotificationDto::new)
						.toList()
		);
	}

	// 알림 단건 조회
	@Operation(summary = "알림 단건 조회", description = "알림 단건 조회")
	@GetMapping("/{notification-id}")
	public RsData<NotificationDto> getUserNotification(
			@PathVariable(name = "notification-id") Long notificationId) {
		Notification notification = notificationService.getNotification(notificationId);
		return new RsData<>(
				"200-1",
				"알림 단건 조회가 완료되었습니다.",
				new NotificationDto(notification)
		);
	}

	// 알림 시간 변경 가능한 목록만 조회
	@Operation(summary = "변경할 알림 조회", description = "알림 시간을 변경 가능한 목록만 조회")
	@GetMapping("/modify")
	public RsData<List<NotificationDto>> getUserModifiableNotification(
			@AuthenticationPrincipal OAuth2User user) {
		String userId = user.getName();
		return new RsData<>(
				"200-1",
				"알림 변경 목록 조회가 완료되었습니다.",
				notificationService.getModifiableNotification(userId)
						.stream()
						.map(NotificationDto::new)
						.toList()
		);
	}


	// 알림 변경 (시간만 변경 가능)
	@Operation(summary = "알림 시간 변경", description = "알림 시간 변경")
	@PutMapping("/{notification-id}/modify")
	public RsData<Void> modifyNotification(
			@PathVariable(name = "notification-id") Long notificationId,
			@RequestBody @Valid ModifyNotificationReqBody modifyNotificationReqBody,
			@AuthenticationPrincipal OAuth2User user) {
		String userId = user.getName();
		notificationService.updateNotification(
				userId, notificationId, modifyNotificationReqBody.notificationTime());

		return new RsData<>(
				"200-1",
				"Notification modified"
		);
	}

	// 알림 설정 업데이트 (이메일, 푸시알림)
	@Operation(summary = "알림 설정", description = "이메일, 푸시알림 설정 업데이트")
	@PatchMapping("/update")
	public RsData<Void> updateNotifications(
			@RequestBody List<NotificationUpdateDto> updateRequests,  // 여러 개의 업데이트를 받을 수 있도록
			@AuthenticationPrincipal OAuth2User user) {
		String userId = user.getName();
		notificationService.updateNotifications(updateRequests, userId);
		return new RsData<>(
				"200-1",
				"Notification updated"
		);
	}
}
