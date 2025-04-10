package com.team01.project.domain.user.service

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class UserEmailService(
    private val javaMailSender: JavaMailSender
) {

    @Value("\${spring.mail.username}")
    lateinit var senderEmail: String

    fun createMail(recipientEmail: String, code: String): MimeMessage {
        val message = javaMailSender.createMimeMessage()
        message.setFrom(InternetAddress(senderEmail, "Music Calendar", "UTF-8"))
        message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail)
        message.subject = "이메일 인증 코드"

        val body = """
            <html>
                <body style='font-family: Arial, sans-serif; background-color: #f1f1f1; padding: 20px;'>
                    <div style='max-width: 600px; margin: 0 auto; padding: 30px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);'>
                        <h2 style='color: #4CAF50; font-size: 24px; text-align: center;'>인증을 위한 이메일 인증번호</h2>
                        <p style='font-size: 16px; color: #333;'>안녕하세요, <strong>회원님</strong>.</p>
                        <p style='font-size: 16px; color: #555;'>요청하신 인증 번호는 아래와 같습니다:</p>
                        <div style='text-align: center; padding: 20px; background-color: #f9f9f9; border-radius: 8px; margin: 20px 0;'>
                            <h1 style='font-size: 36px; color: #4CAF50; font-weight: bold;'>$code</h1>
                            <p style='font-size: 16px; color: #555;'>이 코드를 입력하여 이메일 인증을 완료하세요.</p>
                        </div>
                        <p style='font-size: 14px; color: #777;'>감사합니다!</p>
                        <footer style='font-size: 12px; color: #aaa; text-align: center;'>
                            <p>&copy; 2025 Music Calendar Company</p>
                        </footer>
                    </div>
                </body>
            </html>
        """.trimIndent()

        message.setText(body, "UTF-8", "html")
        return message
    }

    fun sendSimpleMessage(recipientEmail: String, code: String) {
        val message = createMail(recipientEmail, code)
        try {
            javaMailSender.send(message)
        } catch (e: MailException) {
            e.printStackTrace()
            throw IllegalArgumentException("메일 발송 중 오류가 발생했습니다.")
        }
    }
}
