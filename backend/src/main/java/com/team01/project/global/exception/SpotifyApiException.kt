package com.team01.project.global.exception

enum class SpotifyErrorCode(
    val code: String,
    val message: String
) {
    TRACK_NOT_FOUND("404-1", "트랙 정보를 찾을 수 없습니다."),
    ARTIST_GENRE_NOT_FOUND("404-2", "아티스트 장르 정보를 찾을 수 없습니다."),
    PLAYLIST_NOT_FOUND("404-3", "Playlist 정보를 찾을 수 없습니다."),
    INVALID_REQUEST("400-1", "Spotify API 요청 오류 발생"),
    SERVER_ERROR("500-1", "Spotify API 서버 오류 발생"),
    UNKNOWN("500-2", "Spotify API 처리 중 알 수 없는 오류 발생")
}

class SpotifyApiException(
    val errorCode: SpotifyErrorCode,
    cause: Throwable? = null
) : RuntimeException(errorCode.message, cause)
