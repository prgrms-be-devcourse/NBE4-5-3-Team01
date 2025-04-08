package com.team01.project.domain.user.service

import MembershipResponse
import com.team01.project.domain.user.entity.Membership
import com.team01.project.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class MembershipService(
    private val userRepository: UserRepository
) {
    fun getCurrentUserMembership(userId: String): MembershipResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("해당 유저가 존재하지 않습니다.") }

        val membership = user.membership ?: Membership.default(user)

        return MembershipResponse(
            grade = membership.grade,
            startDate = membership.startDate,
            endDate = membership.endDate,
            autoRenew = membership.autoRenew
        )
    }
}