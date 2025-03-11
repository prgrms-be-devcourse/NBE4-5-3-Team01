package com.team01.project.domain.notification.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.team01.project.domain.notification.entity.Notification;
import com.team01.project.domain.notification.event.NotificationUpdatedEvent;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NotificationScheduler {

	private final NotificationService notificationService;
	private final NotificationSender notificationSender; // 알림을 보내는 클래스
	private final ThreadPoolTaskScheduler taskScheduler;
	private ScheduledFuture<?> futureTask; // 현재 예약된 작업

	@PostConstruct
	public void init() {
		// 애플리케이션 시작 시 한번 호출
		System.out.println("애플리케이션 시작! 알림 스케줄링 호출");
		scheduleNotifications();
	}

	@EventListener
	public void handleNotificationUpdated(NotificationUpdatedEvent event) {
		System.out.println("🔔 알림 변경 감지됨! 스케줄링을 다시 설정합니다.");
		scheduleNotifications();
	}

	@Scheduled(cron = "0 0/30 * * * *") // 매 30분마다 실행
	public void scheduleNotifications() {
		// 현재 시간 기준으로 다음 1시간 동안 알림이 있는지 확인
		LocalTime now = LocalTime.from(LocalDateTime.now());
		LocalTime plusMinutes = now.plusMinutes(30);

		List<Notification> notifications = notificationService.getNotificationsBetween(now, plusMinutes);
		System.out.println("현재 시간 기준으로 다음 30분 동안 알림이 있는지 확인. 현재 시간 : " + now + " 다음 체크 시간 : " + plusMinutes);

		if (notifications.isEmpty()) {
			System.out.println("다음 30분 내 알림 없음. 매 30분마다 체크.");
			return;
		}

		// 가장 가까운 알림 시간을 찾기
		LocalTime nextNotificationTime = notifications.stream()
				.map(Notification::getNotificationTime)
				.min(LocalTime::compareTo)
				.orElse(plusMinutes);
		System.out.println("30분내에 다음 알림 시간 : " + nextNotificationTime);

		// 알림을 해당 시간에 보내는 작업을 예약
		scheduleNotificationSending(nextNotificationTime);
	}

	private void scheduleNotificationSending(LocalTime nextNotificationTime) {
		// 알림을 전송할 정확한 시간을 계산
		LocalDateTime notificationDateTime = LocalDateTime.now().withHour(nextNotificationTime.getHour())
				.withMinute(nextNotificationTime.getMinute())
				.withSecond(0)
				.withNano(0);

		Date scheduledTime = Date.from(notificationDateTime.atZone(ZoneId.systemDefault()).toInstant());

		// 다음 알림 시간에 해당하는 알림들 찾기
		List<Notification> notifications = notificationService.getNotificationsByTime(nextNotificationTime);

		// 알림 설정에 따라 전송 여부를 결정
		List<Notification> finalNotifications = notifications.stream()
				// 이메일 또는 푸시 알림이 활성화된 경우에만
				.filter(notification -> notification.isEmailEnabled() || notification.isPushEnabled())
				.collect(Collectors.toList());

		// 알림이 하나도 없으면 작업을 종료
		if (finalNotifications.isEmpty()) {
			System.out.println("활성화된 알림이 없습니다. 알림 전송을 취소합니다.");
			return; // 알림이 없으면 종료
		}

		if (futureTask != null) {
			futureTask.cancel(false); // 기존 예약된 작업 취소
		}
		// 예약된 시간에 알림을 전송하는 작업을 스케줄링
		futureTask = taskScheduler.schedule(() ->
				sendNotifications(finalNotifications, notificationDateTime), scheduledTime);
		System.out.println("알림 전송 예약 시각: " + scheduledTime);
	}

	private void sendNotifications(List<Notification> notifications, LocalDateTime notificationTime) {
		// 알림을 전송
		for (Notification notification : notifications) {
			// 이메일과 푸시알림을 각각 확인해서 전송
			if (notification.isEmailEnabled()) {
				notificationSender.sendEmail(
						notification.getUser(), notification.getTitle(), notification.getMessage());
			}
			if (notification.isPushEnabled()) {
				notificationSender.sendPush(
						notification.getUser(), notification.getTitle(), notification.getMessage(), notificationTime);

			}
		}
		scheduleNotifications();    // 다음 알림이 있나 확인
	}
}
