package com.team01.project.domain.notification.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team01.project.domain.notification.dto.NotificationListDto;
import com.team01.project.domain.notification.service.NotificationListService;
import com.team01.project.global.dto.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "NotificationList", description = "알림리스트 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/notification-lists")
public class NotificationListController {

	private final NotificationListService notificationListService;

	// 현재 로그인한 사용자의 알림 목록 조회
	@Operation(summary = "사용자의 알림리스트 조회", description = "현재 로그인한 사용자가 받은 알림리스트 조회")
	@GetMapping
	public RsData<List<NotificationListDto>> getUserNotifications(
			@AuthenticationPrincipal OAuth2User user) {
		String userId = user.getName();
		return new RsData<>(
				"200-1",
				"사용자에게 온 알림리스트 조회가 완료되었습니다.",
				notificationListService.getUserNotificationLists(userId)
						.stream()
						.map(NotificationListDto::new)
						.sorted(Comparator.comparing(NotificationListDto::notificationTime).reversed())
						.toList()
		);
	}

	// 알림 읽음 처리
	@Operation(summary = "알림 읽음 처리 단건", description = "선택한 알림의 읽음 처리")
	@PatchMapping("/{notificationList-id}")
	public RsData<Void> markNotificationAsRead(
			@PathVariable(name = "notificationList-id") Long notificationListId,
			@AuthenticationPrincipal OAuth2User user) {
		String userId = user.getName();

		notificationListService.markAsRead(notificationListId, userId);
		return new RsData<>(
				"200-1",
				"Notification marked as read"
		);
	}

	// 안읽은 알람 모두 읽음 처리
	@Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림의 읽음 처리")
	@PatchMapping("/mark-all-read")
	public RsData<Void> markAllNotificationAsRead(@AuthenticationPrincipal OAuth2User user) {
		String userId = user.getName();
		notificationListService.markAllAsRead(userId);
		return new RsData<>(
				"200-1",
				"Notification marked All as read"
		);
	}


	// 알림 리스트에서 알림 삭제
	@Operation(summary = "알림 삭제", description = "알림리스트에서 선택한 알림 삭제")
	@DeleteMapping("/{notificationList-id}")
	public RsData<Void> deleteNotification(
			@PathVariable(name = "notificationList-id") Long notificationListId,
			@AuthenticationPrincipal OAuth2User user) {
		String userId = user.getName();
		notificationListService.deleteNotification(notificationListId, userId);
		return new RsData<>("200-1", "Notification deleted");
	}
}
