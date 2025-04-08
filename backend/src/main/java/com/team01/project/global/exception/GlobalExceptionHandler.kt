package com.team01.project.global.exception

import com.team01.project.global.dto.RsData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PermissionDeniedException::class)
    fun handlePermissionDeniedException(ex: PermissionDeniedException): ResponseEntity<RsData<*>> {
        return generateResponse(ex.statusCode, ex.nonNullMessage)
    }

    @ExceptionHandler(CalendarDateAlreadyExistsException::class)
    fun handleCalendarDateAlreadyExistsException(
        ex: CalendarDateAlreadyExistsException
    ): ResponseEntity<RsData<*>> {
        return generateResponse(ex.statusCode, ex.nonNullMessage)
    }

    private fun parseStatusCode(statusCode: String): Int {
        return statusCode.substringBefore("-").toIntOrNull() ?: 500
    }

    private fun generateRsData(statusCode: String, message: String): RsData<*> {
        return RsData<Any>(
            statusCode,
            message
        )
    }

    private fun generateResponse(statusCode: String, message: String): ResponseEntity<RsData<*>> {
        val rsData = generateRsData(statusCode, message)
        return ResponseEntity
            .status(parseStatusCode(statusCode))
            .body(rsData)
    }

}
