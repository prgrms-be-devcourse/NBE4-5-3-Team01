package com.team01.project.domain.notification.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.team01.project.domain.notification.entity.Subscription;
import com.team01.project.domain.notification.repository.SubscriptionRepository;
import com.team01.project.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationSender {

	private final JavaMailSender javaMailSender;
	private final NotificationListService notificationListService;
	private final PushNotificationService pushNotificationService;
	private final SubscriptionRepository subscriptionRepository;

	// 이메일 알림
	public void sendEmail(User user, String title, String message) {
		try {
			// MimeMessage 객체 생성

			var mimeMessage = javaMailSender.createMimeMessage();
			var helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			// 이메일 설정
			helper.setFrom("samvision99@gmail.com");
			// 이메일 설정
			helper.setTo(user.getEmail());  // User의 이메일 주소 사용
			helper.setSubject("Music Calendar 📅 " + title);
			helper.setText(message);

			// 이메일 전송
			javaMailSender.send(mimeMessage);

			System.out.println(
				user.getName() + "님의 " + user.getEmail() + "로 " + title + " 알림이 전송되었습니다. 내용: " + message);
		} catch (Exception e) {
			// 예외 처리
			e.printStackTrace();
		}
	}

	// 푸시 알림
	public void sendPush(User user, String title, String message, LocalDateTime notificationTime) {
		Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserId(user.getId());

		if (subscriptionOpt.isPresent()) {
			Subscription sub = subscriptionOpt.get();
			try {
				pushNotificationService.sendPush(
					sub.getEndpoint(),
					sub.getP256dh(),
					sub.getAuth(),
					title,
					message
				);
				System.out.println(user.getName() + "님에게 " + title + " 푸시알림이 전송되었습니다. 내용: " + message);
			} catch (Exception e) {
				// 예외 처리
				e.printStackTrace();
			}
		} else {
			// subscription이 없을 경우 push 전송은 하지 않음
			System.out.println(user.getName() + "님의 subscription 정보가 없어 푸시알림을 보내지 않습니다.");
		}

		// subscription 여부와 상관없이 notification 기록은 추가
		notificationListService.addNotification(user, title, message, notificationTime);
	}

}
