package com.team01.project.domain.user.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class UserEmailService(
    private val mailSender: JavaMailSender
) {

    @Value("\${spring.mail.username}")
    lateinit var from: String

    fun sendSimpleMessage(to: String, subject: String, text: String) {
        val message = SimpleMailMessage().apply {
            this.from = from
            setTo(to)
            this.subject = subject
            this.text = text
        }
        mailSender.send(message)
    }
}
