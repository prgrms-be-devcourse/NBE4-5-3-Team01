package com.team01.project.global.security

import com.team01.project.global.app.AppConfig
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.firewall.HttpFirewall
import org.springframework.security.web.firewall.StrictHttpFirewall
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val oAuth2UserService: org.springframework.security.oauth2.client.userinfo.OAuth2UserService<org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest, org.springframework.security.oauth2.core.user.OAuth2User>,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler
) {
    private val log = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity, jwtTokenFilter: JwtTokenFilter): SecurityFilterChain {
        log.info("======= START SeCurityFilterChain =======")
        http
            .securityMatcher("/**")
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        "/user/login",
                        "/api/v1/user/logout",
                        "/api/v1/user/refresh",
                        "/api/v1/error",
                        "/login",
                        "/",
                        "/api/v1/follows",
                        "/user/check-duplicate",
                        "/userEmail/emailAuth",
                        "/user/signup"
                    )
                    .permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { userInfo -> userInfo.userService(oAuth2UserService) }
                    .successHandler(oAuth2SuccessHandler)
                    .failureUrl("/loginFailure")
            }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint { request, response, authException ->
                    response.contentType = "application/json; charset=UTF-8"
                    response.characterEncoding = "UTF-8"
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.writer.write("{\"error\": \"Unauthorized - 액세스 토큰이 없거나 로그인 인증되지 않았습니다.\"}")
                }
            }
            .setSharedObject(HttpFirewall::class.java, relaxedHttpFirewall())
        return http.build()
    }

    @Bean
    fun relaxedHttpFirewall(): HttpFirewall {
        val firewall = StrictHttpFirewall()
        // % 인코딩 허용
        firewall.setAllowUrlEncodedPercent(true)
        // 세미콜론 허용
        firewall.setAllowSemicolon(true)
        // 백슬래시 허용
        firewall.setAllowBackSlash(true)
        // URL 인코딩된 슬래시 허용
        firewall.setAllowUrlEncodedSlash(true)
        // URL 인코딩된 더블 슬래시 허용
        firewall.setAllowUrlEncodedDoubleSlash(true)
        // URL 인코딩된 점(.) 허용
        firewall.setAllowUrlEncodedPeriod(true)
        // 모든 호스트 허용 (기본적으로 검사 비활성화)
        firewall.setAllowedHostnames { _ -> true }
        return firewall
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("https://cdpn.io", AppConfig.getSiteFrontUrl())
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")
        configuration.allowCredentials = true
        configuration.allowedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
