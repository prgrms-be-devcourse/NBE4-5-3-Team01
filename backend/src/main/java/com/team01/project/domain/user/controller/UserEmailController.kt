package com.team01.project.domain.user.controller

import com.team01.project.domain.user.service.UserEmailService
import com.team01.project.global.dto.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import kotlin.random.Random

@Tag(name = "Email", description = "메일 API")
@Controller
@RequestMapping("/userEmail")
class UserEmailController(
    private val userEmailService: UserEmailService
) {

    @ResponseBody
    @Operation(summary = "이메일 인증번호 요청 api", description = "이메일 입력 받으면 메일로 인증번호 요청")
    @GetMapping("/emailAuth")
    fun sendEmailAuth(@RequestParam("email") email: String?): RsData<String> {
        if (email.isNullOrBlank()) {
            return RsData("404", "이메일을 입력해주세요.", "")
        }

        val verificationCode = generateVerificationCode()
        // 메일 제목 및 본문 구성
        val subject = "이메일 인증 코드"
        val text = "인증 코드는: $verificationCode"
        // 이메일 전송
        userEmailService.sendSimpleMessage(email, subject, text)
        return RsData("200-1", "이메일 요청 완료", verificationCode)
    }

    private fun generateVerificationCode(): String {
        // 100000 ~ 999999 범위의 6자리 숫자 생성
        val code = Random.nextInt(100000, 1000000)
        return code.toString()
    }
}
