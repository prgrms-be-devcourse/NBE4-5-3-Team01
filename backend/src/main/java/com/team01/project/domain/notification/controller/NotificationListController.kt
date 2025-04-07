package com.team01.project.domain.notification.controller

import com.team01.project.domain.notification.dto.NotificationListDto
import com.team01.project.domain.notification.service.NotificationListService
import com.team01.project.global.dto.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "NotificationList", description = "알림리스트 API")
@RestController
@RequestMapping("/notification-lists")
class NotificationListController(
    private val notificationListService: NotificationListService
) {

    @Operation(summary = "사용자의 알림리스트 조회", description = "현재 로그인한 사용자가 받은 알림리스트 조회")
    @GetMapping
    fun getUserNotifications(@AuthenticationPrincipal user: OAuth2User): RsData<List<NotificationListDto>> {
        val userId = user.name
        val notifications = notificationListService.getUserNotificationLists(userId)
            .map { NotificationListDto(it) }
            .sortedByDescending { it.notificationTime }

        return RsData(
            code = "200-1",
            message = "사용자에게 온 알림리스트 조회가 완료되었습니다.",
            data = notifications
        )
    }

    @Operation(summary = "알림 읽음 처리 단건", description = "선택한 알림의 읽음 처리")
    @PatchMapping("/{notificationList-id}")
    fun markNotificationAsRead(
        @PathVariable("notificationList-id") notificationListId: Long,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<Void> {
        val userId = user.name
        notificationListService.markAsRead(notificationListId, userId)
        return RsData(
            code = "200-1",
            message = "Notification marked as read"
        )
    }

    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림의 읽음 처리")
    @PatchMapping("/mark-all-read")
    fun markAllNotificationAsRead(@AuthenticationPrincipal user: OAuth2User): RsData<Void> {
        val userId = user.name
        notificationListService.markAllAsRead(userId)
        return RsData(
            code = "200-1",
            message = "Notification marked All as read"
        )
    }

    @Operation(summary = "알림 삭제", description = "알림리스트에서 선택한 알림 삭제")
    @DeleteMapping("/{notificationList-id}")
    fun deleteNotification(
        @PathVariable("notificationList-id") notificationListId: Long,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<Void> {
        val userId = user.name
        notificationListService.deleteNotification(notificationListId, userId)
        return RsData(
            code = "200-1",
            message = "Notification deleted"
        )
    }
}
