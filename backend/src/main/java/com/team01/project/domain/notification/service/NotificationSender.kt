package com.team01.project.domain.notification.service

import com.team01.project.domain.notification.repository.SubscriptionRepository
import com.team01.project.domain.user.entity.User
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NotificationSender(
    private val javaMailSender: JavaMailSender,
    private val notificationListService: NotificationListService,
    private val pushNotificationService: PushNotificationService,
    private val subscriptionRepository: SubscriptionRepository
) {

    // 이메일 알림
    fun sendEmail(user: User, title: String, message: String) {
        runCatching {
            val mimeMessage = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

            helper.setFrom("samvision99@gmail.com")
            helper.setTo(user.email)
            helper.setSubject("Music Calendar 📅 $title")
            helper.setText(message)

            javaMailSender.send(mimeMessage)

            println("${user.name}님의 ${user.email}로 $title 알림이 전송되었습니다. 내용: $message")
        }.onFailure {
            it.printStackTrace()
        }
    }

    // 푸시 알림
    fun sendPush(user: User, title: String, message: String, notificationTime: LocalDateTime) {
        val subscription = subscriptionRepository.findByUserId(user.id)

        subscription.ifPresentOrElse({ sub ->
            runCatching {
                pushNotificationService.sendPush(
                    sub.endpoint,
                    sub.p256dh,
                    sub.auth,
                    title,
                    message
                )
                println("${user.name}님에게 $title 푸시알림이 전송되었습니다. 내용: $message")
            }.onFailure {
                it.printStackTrace()
            }
        }, {
            println("${user.name}님의 subscription 정보가 없어 푸시알림을 보내지 않습니다.")
        })

        // 알림 기록은 항상 저장
        notificationListService.addNotification(user, title, message, notificationTime)
    }
}
