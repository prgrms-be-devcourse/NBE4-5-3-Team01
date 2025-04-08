package com.team01.project.global.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Base64
import java.util.Date

@Component
class JwtTokenProvider {

    companion object {
        private const val SECRET_KEY_STRING = "aXlvdXZvLWNvc2VjLXJhbmdvbGV0LXNlY3JldC1rZXkta2V5LWZvci1qd3Q="
        private val SECRET_KEY: Key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY_STRING))
        private const val VALIDITY_IN_MS: Long = 3600000L // 1시간
        private val log = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    }

    private var refreshTokenValidity: Long = 1000 * 60 * 60 * 24 * 7 // 7일

    // JWT 토큰 생성 (액세스 토큰)
    fun generateJwtToken(userId: String, spotifyAccessToken: String): String {
        val claims: MutableMap<String, Any> = HashMap()
        claims["sub"] = userId
        claims["spotifyToken"] = spotifyAccessToken

        val now = LocalDateTime.now()
        val validity = now.plus(Duration.ofMillis(VALIDITY_IN_MS))

        val issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant())
        val expiration = Date.from(validity.atZone(ZoneId.systemDefault()).toInstant())

        return Jwts.builder()
            .addClaims(claims)
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact()
    }

    // 리프레시 토큰 생성
    fun generateRefreshToken(userId: String): String {
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenValidity))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact()
    }

    // JWT 토큰으로부터 사용자 ID 추출
    fun getUserIdFromToken(token: String): String {
        val parser: JwtParser = Jwts.parser().setSigningKey(SECRET_KEY).build()
        val claims: Claims = parser.parseClaimsJws(token).body
        return claims.subject
    }

    // JWT 토큰 유효성 검증
    fun validateToken(token: String): Boolean {
        return try {
            val parser: JwtParser = Jwts.parser().setSigningKey(SECRET_KEY).build()
            parser.parseClaimsJws(token)
            true
        } catch (e: ExpiredJwtException) {
            log.info("JWT 만료")
            false
        } catch (e: SignatureException) {
            log.info("JWT 서명 검증 실패")
            false
        } catch (e: MalformedJwtException) {
            log.info("JWT 형식이 올바르지 않음")
            false
        } catch (e: Exception) {
            log.info("JWT 검증 중 알 수 없는 오류 발생:{}", e.message)
            false
        }
    }

    /**
     * 주어진 JWT에서 'spotifyToken' 클레임을 추출합니다.
     *
     * @param jwtToken 클라이언트로부터 전달받은 JWT 토큰
     * @return spotifyToken 값이 존재하면 반환, 그렇지 않으면 null
     */
    fun extractSpotifyToken(jwtToken: String): String? {
        return try {
            val parser: JwtParser = Jwts.parser().setSigningKey(SECRET_KEY).build()
            val claims: Claims = parser.parseClaimsJws(jwtToken).body
            if (claims["spotifyToken"] != null) {
                claims.get("spotifyToken", String::class.java)
            } else {
                log.info("현재 토큰에는 스포티파이 토큰이 포함되어 있지 않습니다.")
                null
            }
        } catch (e: Exception) {
            log.info("Error parsing JWT:{}", e.message)
            null
        }
    }
}
