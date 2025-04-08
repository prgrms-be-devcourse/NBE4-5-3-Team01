package com.team01.project.global.exception

class PermissionDeniedException(
    val statusCode: String,
    message: String
) : RuntimeException(message) {
    val nonNullMessage: String = message
}
