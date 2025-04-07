package com.team01.project.domain.notification.service

import jakarta.annotation.PostConstruct
import nl.martijndwars.webpush.Notification
import nl.martijndwars.webpush.PushService
import nl.martijndwars.webpush.Utils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Security

@Service
class PushNotificationService {

    private lateinit var pushService: PushService

    // application.yml에서 VAPID 키 주입
    @Value("\${push.vapid.publicKey}")
    private lateinit var publicKey: String

    @Value("\${push.vapid.privateKey}")
    private lateinit var privateKey: String

    @PostConstruct
    fun init() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }

        try {
            pushService = PushService()
                .setPublicKey(Utils.loadPublicKey(publicKey))
                .setPrivateKey(Utils.loadPrivateKey(privateKey))
        } catch (e: Exception) {
            throw RuntimeException("PushNotificationService 초기화 실패", e)
        }
    }

    @Throws(Exception::class)
    fun sendPush(
        endpoint: String,
        userPublicKey: String,
        auth: String,
        title: String,
        message: String
    ) {
        val jsonPayload = """{"title": "$title", "message": "$message"}"""
        val notification = Notification(endpoint, userPublicKey, auth, jsonPayload.toByteArray())
        pushService.send(notification)
    }
}
