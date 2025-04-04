package com.team01.project.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.team01.project.domain.user.service.UserEmailService;
import com.team01.project.global.dto.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Email", description = "메일 API")
@Controller
@RequestMapping("/userEmail")
@RequiredArgsConstructor
public class UserEmailController {
	private final UserEmailService userEmailService;

	@ResponseBody
	@Operation(summary = "이메일 인증번호 요청 api", description = "이메일 입력 받으면 메일로 인증번호 요청")
	@GetMapping("/emailAuth")
	public RsData<String> sendEmailAuth(@RequestParam("email") String email) {
		if (email == null || email.trim().isEmpty()) {
			return new RsData<>("404", "이메일을 입력해주세요.", "");
		}

		String verificationCode = generateVerificationCode();

		// 메일 제목 및 본문 구성
		String subject = "이메일 인증 코드";
		String text = "인증 코드는: " + verificationCode;

		// 이메일 전송
		userEmailService.sendSimpleMessage(email, subject, text);
		return new RsData<>("200-1", "이메일 요청 완료", verificationCode);
	}

	private String generateVerificationCode() {
		// 100000 ~ 999999 범위의 6자리 숫자 생성
		int code = (int)(Math.random() * 900000) + 100000;
		return String.valueOf(code);
	}
}
