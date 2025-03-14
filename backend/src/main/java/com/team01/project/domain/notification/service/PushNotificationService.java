package com.team01.project.domain.notification.service;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;

@Service
public class PushNotificationService {

	private PushService pushService;

	// application.yml에서 VAPID 키 값을 주입받습니다.
	@Value("${push.vapid.publicKey}")
	private String publicKey;

	@Value("${push.vapid.privateKey}")
	private String privateKey;


	@PostConstruct
	public void init() {

		// Bouncy Castle 프로바이더 등록
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		try {
			this.pushService = new PushService()
					.setPublicKey(Utils.loadPublicKey(publicKey))
					.setPrivateKey(Utils.loadPrivateKey(privateKey));
		} catch (Exception e) {
			throw new RuntimeException("PushNotificationService 초기화 실패", e);
		}
	}

	// 실제 푸시 메시지를 보내는 메서드
	public void sendPush(String endpoint, String userPublicKey, String auth, String payload) throws Exception {
		// JSON 형태로 감싸서 보내기
		String jsonPayload = String.format("{\"message\": \"%s\"}", payload);

		// Notification 객체 생성 (구독 정보와 payload를 바이트 배열로 변환)
		Notification notification = new Notification(endpoint, userPublicKey, auth, jsonPayload.getBytes());
		pushService.send(notification);
	}

	// 실제 푸시 메시지를 보내는 메서드
	public void sendPush(
			String endpoint, String userPublicKey, String auth, String title, String message) throws Exception {
		String jsonPayload = String.format("{\"title\": \"%s\", \"message\": \"%s\"}", title, message);

		// Notification 객체 생성
		Notification notification = new Notification(endpoint, userPublicKey, auth, jsonPayload.getBytes());

		// 여기서 pushService.send() 메서드로 푸시 알림 전송
		pushService.send(notification);
	}
}
