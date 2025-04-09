package com.team01.project.global.exception

import com.team01.project.global.dto.RsData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PermissionDeniedException::class)
    fun handlePermissionDeniedException(ex: PermissionDeniedException): ResponseEntity<RsData<Unit>> {
        return generateResponse(ex.statusCode, ex.nonNullMessage)
    }

    @ExceptionHandler(CalendarDateAlreadyExistsException::class)
    fun handleCalendarDateAlreadyExistsException(
        ex: CalendarDateAlreadyExistsException
    ): ResponseEntity<RsData<Unit>> {
        return generateResponse(ex.statusCode, ex.nonNullMessage)
    }

    @ExceptionHandler(MembershipException::class)
    fun handleMembershipException(e: MembershipException): RsData<Nothing> {
        return RsData(
            code = e.errorCode.code,
            msg = e.errorCode.message,
            data = null
        )
    }

    @ExceptionHandler(MusicException::class)
    fun handleMusicException(e: MusicException): RsData<Nothing> {
        return RsData(
            code = e.errorCode.code,
            msg = e.errorCode.message,
            data = null
        )
    }

    @ExceptionHandler(SpotifyApiException::class)
    fun handleSpotifyApiException(e: SpotifyApiException): RsData<Nothing> {
        return RsData(
            code = e.errorCode.code,
            msg = e.errorCode.message,
            data = null
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(e: Exception): RsData<Nothing> {
        return RsData(
            code = "500",
            msg = "예상치 못한 서버 오류가 발생했습니다.",
            data = null
        )
    }

    private fun parseStatusCode(statusCode: String): Int {
        return statusCode.substringBefore("-").toIntOrNull() ?: 500
    }

    private fun generateRsData(statusCode: String, message: String): RsData<Unit> {
        return RsData(
            statusCode,
            message
        )
    }

    private fun generateResponse(statusCode: String, message: String): ResponseEntity<RsData<Unit>> {
        val rsData = generateRsData(statusCode, message)

        return ResponseEntity
            .status(parseStatusCode(statusCode))
            .body(rsData)
    }
}
