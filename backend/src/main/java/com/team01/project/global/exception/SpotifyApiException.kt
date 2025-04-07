package com.team01.project.global.exception

class SpotifyApiException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
