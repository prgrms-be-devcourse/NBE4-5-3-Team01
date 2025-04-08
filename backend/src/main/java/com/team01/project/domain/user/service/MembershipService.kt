package com.team01.project.domain.user.service

import MembershipResponse
import com.team01.project.domain.user.entity.Membership
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.exception.MembershipErrorCode
import com.team01.project.global.exception.MembershipException
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MembershipService(
    private val userRepository: UserRepository
) {
    fun getCurrentUserMembership(userId: String): MembershipResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { MembershipException(MembershipErrorCode.USER_NOT_FOUND) }

        val membership = user.membership
            ?: throw MembershipException(MembershipErrorCode.MEMBERSHIP_NOT_FOUND)

        return MembershipResponse(
            grade = membership.grade,
            startDate = membership.startDate,
            endDate = membership.endDate,
            autoRenew = membership.autoRenew
        )
    }

    fun cancelMembership(userId: String) {
        val user = userRepository.findById(userId)
            .orElseThrow { MembershipException(MembershipErrorCode.USER_NOT_FOUND) }

        val membership = user.membership
            ?: throw MembershipException(MembershipErrorCode.MEMBERSHIP_NOT_FOUND)

        if (membership.grade != "premium") {
            throw MembershipException(MembershipErrorCode.NOT_PREMIUM)
        }

        membership.grade = "basic"
        membership.autoRenew = false

        userRepository.save(user)
    }

    fun upgradeToPremium(userId: String) {
        val user = userRepository.findById(userId)
            .orElseThrow { MembershipException(MembershipErrorCode.USER_NOT_FOUND) }

        val membership = user.membership
            ?: throw MembershipException(MembershipErrorCode.MEMBERSHIP_NOT_FOUND)

        if (membership.grade == "premium") {
            throw MembershipException(MembershipErrorCode.ALREADY_PREMIUM)
        }

        membership.grade = "premium"
        membership.startDate = LocalDate.now()
        membership.endDate = LocalDate.now().plusMonths(1)
        membership.autoRenew = true
        membership.count += 1

        userRepository.save(user)
    }

    fun initMembership(userId: String): Boolean {
        val user = userRepository.findById(userId)
            .orElseThrow { MembershipException(MembershipErrorCode.USER_NOT_FOUND) }

        if (user.membership != null) return false

        val membership = Membership.default(user)
        user.membership = membership
        userRepository.save(user)
        return true
    }
}