package com.team01.project.domain.notification.service;

import java.time.LocalTime;
import java.util.concurrent.ScheduledFuture;

// 예약된 작업과 시간을 함께 관리할 클래스
public record CustomScheduledTask(ScheduledFuture<?> futureTask, LocalTime scheduledTime) {

}