package com.team01.project.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.team01.project.global.dto.RsData;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PermissionDeniedException.class)
	public ResponseEntity<RsData<?>> handlePermissionDeniedException(PermissionDeniedException ex) {
		return generateResponse(ex.getStatusCode(), ex.getMessage());
	}

	@ExceptionHandler(CalendarDateAlreadyExistsException.class)
	public ResponseEntity<RsData<?>> handleCalendarDateAlreadyExistsException(
		CalendarDateAlreadyExistsException ex
	) {
		return generateResponse(ex.getStatusCode(), ex.getMessage());
	}

	private int parseStatusCode(String statusCode) {
		return Integer.parseInt(statusCode.split("-")[0]);
	}

	private RsData<?> generateRsData(String statusCode, String message) {
		return new RsData<>(
			statusCode,
			message
		);
	}

	private ResponseEntity<RsData<?>> generateResponse(String statusCode, String message) {
		RsData<?> rsData = generateRsData(statusCode, message);
		return ResponseEntity
			.status(parseStatusCode(statusCode))
			.body(rsData);
	}

}