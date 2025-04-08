package com.team01.project.domain.user.service

import com.team01.project.domain.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    companion object {
        private val log = LoggerFactory.getLogger(CustomUserDetailsService::class.java)
    }

    override fun loadUserByUsername(userId: String): UserDetails {
        log.info("======= START CustomUserDetailsService.loadUserByUsername =======")
        val user = userRepository.findById(userId)
            .orElseThrow { UsernameNotFoundException("사용자를 찾을 수 없습니다: $userId") }

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.id) // userId를 username으로 설정
            .password("")      // OAuth 로그인에서는 비밀번호가 필요 없음
            .authorities("USER") // 기본 권한 설정
            .build()
    }
}