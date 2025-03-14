package com.team01.project.domain.notification.service;

import java.time.LocalDateTime;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.team01.project.domain.user.entity.User;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NotificationSender {

	private final JavaMailSender javaMailSender;
	private final NotificationListService notificationListService;

	public void sendEmail(User user, String title, String message) {
		try {
			// MimeMessage 객체 생성
			var mimeMessage = javaMailSender.createMimeMessage();
			var helper = new MimeMessageHelper(mimeMessage, true);

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

	public void sendPush(User user, String title, String message, LocalDateTime notificationTime) {
		try {
			notificationListService.addNotification(user, title, message, notificationTime);

			System.out.println(user.getName() + "님에게 " + title + " 푸시알림이 전송되었습니다. 내용: " + message);
		} catch (Exception e) {
			// 예외 처리
			e.printStackTrace();
		}
	}
}
