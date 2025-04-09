package com.team01.project.global.exception

enum class MembershipErrorCode(
    val code: String,
    val message: String
) {
    USER_NOT_FOUND("404-1", "해당 사용자를 찾을 수 없습니다."),
    MEMBERSHIP_NOT_FOUND("404-2", "요금제 정보가 없습니다."),
    NOT_PREMIUM("400-1", "프리미엄 요금제가 아닙니다."),
    UNAUTHORIZED("401-1", "로그인 정보가 없습니다."),
    ALREADY_PREMIUM("400-2", "이미 프리미엄 요금제를 사용 중입니다.")
}

class MembershipException(
    val errorCode: MembershipErrorCode
) : RuntimeException(errorCode.message)
