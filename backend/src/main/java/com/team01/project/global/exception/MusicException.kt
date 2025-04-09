package com.team01.project.global.exception

enum class MusicErrorCode(
    val code: String,
    val message: String
) {
    NOT_FOUND("404-1", "해당 음악을 찾을 수 없습니다."),
    INVALID_SPOTIFY_TOKEN("401-1", "Spotify 토큰이 유효하지 않습니다."),
    PLAYLIST_LIMIT_EXCEEDED("400-1", "20곡 이하의 플레이리스트만 추가할 수 있습니다.")
}

class MusicException(
    val errorCode: MusicErrorCode
) : RuntimeException(errorCode.message)
