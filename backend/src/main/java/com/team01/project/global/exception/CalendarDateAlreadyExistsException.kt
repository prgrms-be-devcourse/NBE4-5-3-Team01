package com.team01.project.global.exception

class CalendarDateAlreadyExistsException(
    val statusCode: String,
    message: String
) : RuntimeException(message) {
    val nonNullMessage: String = message
}
