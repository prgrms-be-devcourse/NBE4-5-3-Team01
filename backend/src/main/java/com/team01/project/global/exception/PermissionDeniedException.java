package com.team01.project.global.exception;

import lombok.Getter;

@Getter
public class PermissionDeniedException extends RuntimeException {
	private final String statusCode;

	public PermissionDeniedException(String statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}
}