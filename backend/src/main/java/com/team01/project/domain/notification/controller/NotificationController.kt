package com.team01.project.domain.notification.controller

import com.team01.project.domain.notification.dto.ModifyNotificationReqBody
import com.team01.project.domain.notification.dto.NotificationDto
import com.team01.project.domain.notification.dto.NotificationUpdateDto
import com.team01.project.domain.notification.entity.Notification
import com.team01.project.domain.notification.service.NotificationService
import com.team01.project.global.dto.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @Operation(summary = "전체 알림 조회", description = "모든 사용자의 알림 목록 조회")
    @GetMapping
    fun getNotifications(): RsData<List<NotificationDto>> {
        val notifications = notificationService.getAllNotifications()
            .map { NotificationDto(it) }

        return RsData(
            code = "200-1",
            msg = "전체 알림 목록 조회가 완료되었습니다.",
            data = notifications
        )
    }

    @Operation(summary = "사용자의 알림 조회", description = "한 사용자의 알림 목록 조회")
    @GetMapping("/lists")
    fun getUserNotifications(@AuthenticationPrincipal user: OAuth2User): RsData<List<NotificationDto>> {
        val userId = user.name
        val notifications = notificationService.getUserNotifications(userId)
            .map { NotificationDto(it) }

        return RsData(
            code = "200-1",
            msg = "사용자의 알림 목록 조회가 완료되었습니다.",
            data = notifications
        )
    }

    @Operation(summary = "알림 단건 조회", description = "알림 단건 조회")
    @GetMapping("/{notification-id}")
    fun getUserNotification(@PathVariable("notification-id") notificationId: Long): RsData<NotificationDto> {
        val notification: Notification = notificationService.getNotification(notificationId)
        return RsData(
            code = "200-1",
            msg = "알림 단건 조회가 완료되었습니다.",
            data = NotificationDto(notification)
        )
    }

    @Operation(summary = "변경할 알림 조회", description = "알림 시간을 변경 가능한 목록만 조회")
    @GetMapping("/modify")
    fun getUserModifiableNotification(@AuthenticationPrincipal user: OAuth2User): RsData<List<NotificationDto>> {
        val userId = user.name
        val notifications = notificationService.getModifiableNotification(userId)
            .map { NotificationDto(it) }

        return RsData(
            code = "200-1",
            msg = "알림 변경 목록 조회가 완료되었습니다.",
            data = notifications
        )
    }

    @Operation(summary = "알림 시간 변경", description = "알림 시간 변경")
    @PutMapping("/{notification-id}/modify")
    fun modifyNotification(
        @PathVariable("notification-id") notificationId: Long,
        @RequestBody @Valid
        body: ModifyNotificationReqBody,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<Void> {
        val userId = user.name
        notificationService.updateNotification(userId, notificationId, body.notificationTime)

        return RsData(
            code = "200-1",
            msg = "Notification modified"
        )
    }

    @Operation(summary = "알림 설정", description = "이메일, 푸시알림 설정 업데이트")
    @PatchMapping("/update")
    fun updateNotifications(
        @RequestBody updateRequests: List<NotificationUpdateDto>,
        @AuthenticationPrincipal user: OAuth2User
    ): RsData<Void> {
        val userId = user.name
        notificationService.updateNotifications(updateRequests, userId)

        return RsData(
            code = "200-1",
            msg = "Notification updated"
        )
    }
}
