package com.team01.project.domain.notification.constants

object NotificationMessages {

    val DEFAULT_MESSAGES: Map<String, String> = linkedMapOf(
        "DAILY CHALLENGE" to "%s님, 하루 한 곡 기록 도전해보세요! 📅",       // 기본 설정 21:00
        "SHARE MUSIC" to "%s님, 오늘 등록한 음악을 공유해보세요! 🎶",        // 음악을 캘린더에 기록 시
        "BUILD PLAYLIST" to "%s님, 회원님이 오늘 등록한 음악을 공유해보세요! 🎶", // 18:00
        "YEAR HISTORY" to "%s님, 1년 전에는 어떤 음악을 기록했는지 확인해보세요!", // 09:00
        "FOLLOWING" to "%s님이 회원님을 팔로우하기 시작했습니다."           // 팔로우 시
    )
}
