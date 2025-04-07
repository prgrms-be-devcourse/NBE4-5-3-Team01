package com.team01.project.global.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.team01.project.global.dto.RsData;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class ResponseAspect {

	private final HttpServletResponse response;

	@Around("""
			(
					within
				(
					@org.springframework.web.bind.annotation.RestController *
				)
				&&
				(
					@annotation(org.springframework.web.bind.annotation.GetMapping)
					||
					@annotation(org.springframework.web.bind.annotation.PostMapping)
					||
					@annotation(org.springframework.web.bind.annotation.PutMapping)
					||
					@annotation(org.springframework.web.bind.annotation.DeleteMapping)
					||
					@annotation(org.springframework.web.bind.annotation.PatchMapping)
				)
			)
			||
			@annotation(org.springframework.web.bind.annotation.ResponseBody)
			""")
	public Object responseAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		Object rst = joinPoint.proceed();

		if (rst instanceof RsData rsData) {
			int statusCode = rsData.getStatusCode();
			response.setStatus(statusCode);
		}

		return rst;
	}
}