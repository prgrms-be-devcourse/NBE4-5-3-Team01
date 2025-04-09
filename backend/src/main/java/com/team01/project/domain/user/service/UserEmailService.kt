package com.team01.project.domain.user.service

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class UserEmailService(
    private val mailSender: JavaMailSender
) {

    @Value("\${spring.mail.username}")
    lateinit var from: String

    fun sendSimpleMessage(to: String, subject: String, text: String) {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, false, "UTF-8")

        helper.setFrom(InternetAddress(from, "YourAppName", "UTF-8")) // 디코딩 오류 방지
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(text, false) // HTML 사용 안 할 경우 false

        mailSender.send(mimeMessage)
    }
}
