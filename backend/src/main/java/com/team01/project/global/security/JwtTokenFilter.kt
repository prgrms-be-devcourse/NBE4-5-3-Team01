package com.team01.project.global.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtTokenFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    companion object {
        private val log = LoggerFactory.getLogger(JwtTokenFilter::class.java)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        log.info("======= START JwtTokenFilter.doFilterInternal =======")

        val header = request.getHeader("Authorization")
        log.info("Header value:{}", header)

        var token: String? = null

        // Authorization 헤더에 "Bearer "로 시작하면 그 뒤의 토큰 값을 추출합니다.
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7)
        }

        // 헤더에 토큰이 없으면 쿠키에서 "accessToken"을 찾습니다.
        if (token == null) {
            log.info("Authorization 헤더에 토큰이 없으므로 쿠키에서 토큰을 찾습니다.")
            request.cookies?.forEach { cookie ->
                if (cookie.name == "accessToken") {
                    token = cookie.value
                    return@forEach
                }
            }
        }

        // 토큰이 없는 경우, 필터 체인을 계속 진행하고 종료합니다.
        if (token == null) {
            log.info("토큰을 찾을 수 없습니다.")
            chain.doFilter(request, response)
            return
        }

        // 여기서부터는 token이 null이 아님을 보장하기 위해 non-null 변수로 할당
        val nonNullToken = token!!

        // 토큰 유효성 검증
        val isValid = jwtTokenProvider.validateToken(nonNullToken)
        log.info("JWT 검증 결과:{} ", isValid)

        if (!isValid) {
            response.contentType = "application/json; charset=UTF-8"
            response.characterEncoding = "UTF-8"
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("{\"error\": \"jwt token이 검증되지 않음\"}")
            return
        }

        val spotifyToken = jwtTokenProvider.extractSpotifyToken(nonNullToken) ?: ""
        val userId = jwtTokenProvider.getUserIdFromToken(nonNullToken)

        // 사용자 정보를 담은 OAuth2User 생성
        val oAuth2User: OAuth2User = DefaultOAuth2User(
            setOf(SimpleGrantedAuthority("ROLE_USER")),
            mapOf("id" to userId, "spotifyToken" to spotifyToken),
            "id"
        )

        // OAuth2User 기반의 인증 객체 생성
        val auth = OAuth2AuthenticationToken(
            oAuth2User,
            oAuth2User.authorities,
            "spotify" // OAuth2 로그인 제공자명
        )

        // SecurityContext에 인증 정보를 설정
        SecurityContextHolder.getContext().authentication = auth

        chain.doFilter(request, response)
    }
}
