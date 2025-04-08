package com.team01.project.global.exception;

import lombok.Getter;

@Getter
public class CalendarDateAlreadyExistsException extends RuntimeException {
	private final String statusCode;

	public CalendarDateAlreadyExistsException(String statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}
}