package com.team01.project.domain.notification.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.team01.project.domain.notification.entity.Notification;
import com.team01.project.domain.notification.event.NotificationFollowEvent;
import com.team01.project.domain.notification.event.NotificationInitEvent;
import com.team01.project.domain.notification.event.NotificationRecordEvent;
import com.team01.project.domain.notification.event.NotificationUpdatedEvent;
import com.team01.project.domain.user.entity.User;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NotificationScheduler {

	private final NotificationService notificationService;
	private final NotificationSender notificationSender; // 알림을 보내는 클래스
	private final ThreadPoolTaskScheduler taskScheduler;
	private final ThreadPoolTaskScheduler separateTaskScheduler;
	private final List<CustomScheduledTask> scheduledTasks = new ArrayList<>(); // 여러 예약 작업을 저장할 리스트

	@PostConstruct
	public void init() {
		// 애플리케이션 시작 시 한번 호출
		System.out.println("애플리케이션 시작! 알림 스케줄링 호출");
		scheduleNotifications();
	}

	@Async
	@EventListener
	public void handleNotificationUpdated(NotificationUpdatedEvent event) {
		System.out.println("🔔 알림 변경 감지됨! 스케줄링을 다시 설정합니다.");
		scheduleNotifications();
	}

	@Scheduled(cron = "0 29,59 * * * *") // 매 29분, 59분마다 실행
	public void scheduleNotifications() {
		// 현재 시간 기준으로 다음 30분 동안 알림이 있는지 확인
		LocalTime now = LocalTime.from(LocalDateTime.now());
		LocalTime plusMinutes = now.plusMinutes(30);

		List<LocalTime> notificationTimes = notificationService.getNotificationTimeBetween(now, plusMinutes);

		System.out.println("현재 시간 기준으로 다음 30분 동안 알림이 있는지 확인. 현재 시간 : "
				+ now + " 다음 체크 시간 : " + plusMinutes);

		if (notificationTimes.isEmpty()) {
			System.out.println("다음 30분 내 알림 없음. 매 30분마다 체크.");
			return;
		}

		// 기존 예약된 작업 중 완료된 것들만 삭제하고, 나머지는 그대로 두기
		cancelCompletedScheduledTasks();


		for (LocalTime notificationTime : notificationTimes) {
			scheduleNotificationSending(notificationTime);
		}
	}

	private void cancelCompletedScheduledTasks() {
		// 예약된 작업들 중 시간이 이미 지나거나 완료된 작업만 취소
		Iterator<CustomScheduledTask> iterator = scheduledTasks.iterator();
		while (iterator.hasNext()) {
			CustomScheduledTask task = iterator.next();
			if (task.futureTask().isDone() || LocalTime.now().isAfter(task.scheduledTime())) {
				iterator.remove();
			}
		}
	}

	private void scheduleNotificationSending(LocalTime notificationTime) {
		// 알림을 전송할 정확한 시간을 계산
		LocalDateTime notificationDateTime = LocalDateTime.now().withHour(notificationTime.getHour())
				.withMinute(notificationTime.getMinute())
				.withSecond(0)
				.withNano(0);

		Date scheduledTime = Date.from(notificationDateTime.atZone(ZoneId.systemDefault()).toInstant());

		// 다음 알림 시간에 해당하는 알림들 찾기
		List<Notification> notifications = notificationService.getNotificationsByTime(notificationTime);

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

		// 알림 전송 작업을 예약
		ScheduledFuture<?> futureTask = taskScheduler.schedule(() ->
				sendNotifications(finalNotifications, notificationDateTime), scheduledTime);

		// 새 알림을 시간에 맞게 리스트에 삽입
		insertTaskInOrder(futureTask, notificationTime);
		System.out.println("알림 전송 예약 시각: " + scheduledTime);
	}

	// 알림을 시간에 맞게 리스트에 삽입하는 메서드
	private void insertTaskInOrder(ScheduledFuture<?> futureTask, LocalTime notificationTime) {
		CustomScheduledTask scheduledTask = new CustomScheduledTask(futureTask, notificationTime);
		// scheduledTasks 리스트에서 알림 전송 시간을 기준으로 올바른 위치에 삽입
		int index = 0;
		while (index < scheduledTasks.size() && scheduledTasks.get(index).scheduledTime().isBefore(notificationTime)) {
			index++;
		}
		scheduledTasks.add(index, scheduledTask); // 시간 순으로 삽입
	}

	private void sendNotification(Notification notification, LocalDateTime notificationTime) {
		// 알림을 전송
		// 이메일과 푸시알림을 각각 확인해서 전송
		if (notification.isPushEnabled()) {
			notificationSender.sendPush(
					notification.getUser(), notification.getTitle(), notification.getMessage(), notificationTime);
		}
		if (notification.isEmailEnabled()) {
			notificationSender.sendEmail(
					notification.getUser(), notification.getTitle(), notification.getMessage());
		}
	}

	private void sendNotifications(List<Notification> notifications, LocalDateTime notificationTime) {
		// 알림을 전송
		for (Notification notification : notifications) {
			// 이메일과 푸시알림을 각각 확인해서 전송
			if (notification.isPushEnabled()) {
				notificationSender.sendPush(
						notification.getUser(), notification.getTitle(), notification.getMessage(), notificationTime);
			}
			if (notification.isEmailEnabled()) {
				notificationSender.sendEmail(
						notification.getUser(), notification.getTitle(), notification.getMessage());
			}
		}
	}

	@Async
	@EventListener
	public void handleNotificationInit(NotificationInitEvent event) {
		System.out.println("🔔 새로운 유저 로그인!");
		scheduleNotificationInitSending(event.getTime(), event.getUser());
	}

	private void scheduleNotificationInitSending(LocalTime notificationTime, User user) {
		// 첫 번째 알림 예약 (2분 후)
		scheduleSingleNotification(
				user,
				notificationTime.plusMinutes(2),
				"WELCOME",
				"%s님, 환영합니다! 🎉".formatted(user.getName())
		);

		// 두 번째 알림 예약 (3분 후)
		scheduleSingleNotification(
				user,
				notificationTime.plusMinutes(5),
				"START RECORDING",
				"%s님, 음악 기록을 시작해보세요! 🎵".formatted(user.getName())
		);
	}

	private void scheduleSingleNotification(User user, LocalTime notificationTime, String title, String message) {
		LocalDateTime notificationDateTime = LocalDateTime.now().withHour(notificationTime.getHour())
				.withMinute(notificationTime.getMinute())
				.withSecond(0)
				.withNano(0);

		Date scheduledTime = Date.from(notificationDateTime.atZone(ZoneId.systemDefault()).toInstant());

		Notification notification =
				Notification.builder()
						.user(user)
						.notificationTime(notificationTime)
						.title(title)
						.message(message)
						.build();

		// 알림 전송 작업을 예약
		ScheduledFuture<?> futureTask = separateTaskScheduler.schedule(() ->
				sendNotification(notification, notificationDateTime), scheduledTime);

		// 새 알림을 시간에 맞게 리스트에 삽입
		insertTaskInOrder(futureTask, notificationTime);

		System.out.println("알림 전송 예약 시각: " + scheduledTime);
	}

	@Async
	@EventListener
	public void handleNotificationAsync(NotificationFollowEvent event) {
		System.out.println("🔔 새로운 팔로우 알림!");
		scheduleNotificationFollowSending(event.getTime(), event.getToUser(),
				"FOLLOWING", "%s님이 회원님을 팔로우하기 시작했습니다.".formatted(event.getFromUser().getName()));
	}

	@Async
	@EventListener
	public void handleNotificationAsync(NotificationRecordEvent event) {
		System.out.println("🔔 " + event.getUser().getName() + "님의 새로운 음악 등록 알림!");
		scheduleNotificationFollowSending(event.getTime(), event.getUser(),
				"SHARE MUSIC", "%s님, 오늘도 음악을 등록하셨네요! 회원님이 오늘 등록한 음악을 공유해보세요! 🎶".formatted(event.getUser().getName()));
	}

	private void scheduleNotificationFollowSending(
			LocalTime notificationTime, User user, String title, String message) {
		LocalDateTime notificationDateTime = LocalDateTime.now().withHour(notificationTime.getHour())
				.withMinute(notificationTime.getMinute())
				.withSecond(0)
				.withNano(0);

		// 알림 생성
		Notification notification = Notification.builder()
				.user(user)
				.notificationTime(notificationTime)
				.title(title)
				.message(message)
				.build();

		sendNotificationAsync(notification, notificationDateTime);
	}

	private void sendNotificationAsync(Notification notification, LocalDateTime notificationTime) {
		// 알림을 전송
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
}
