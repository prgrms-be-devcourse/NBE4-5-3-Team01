package com.team01.project.domain.notification.dto;

import java.util.List;

public record NotificationUpdateRequest(List<NotificationUpdateDto> notifications) {
}