package com.team01.project.domain.user.service

import MembershipDto
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
    fun getCurrentUserMembership(userId: String): MembershipDto {
        val user = userRepository.findById(userId)
            .orElseThrow { MembershipException(MembershipErrorCode.USER_NOT_FOUND) }

        val membership = user.membership
            ?: throw MembershipException(MembershipErrorCode.MEMBERSHIP_NOT_FOUND)

        return MembershipDto(
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

//        membership.grade = "basic"
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

    fun getAllMemberships(): List<Map<String, Any>> {
        return userRepository.findAll().mapNotNull { user ->
            val membership = user.membership ?: return@mapNotNull null

            mapOf(
                "id" to membership.id,
                "userId" to user.id,
                "userName" to user.name,
                "grade" to membership.grade,
                "startDate" to membership.startDate.toString(),
                "endDate" to membership.endDate.toString(),
                "months" to membership.count,
                "autoRenew" to membership.autoRenew
            )
        }
    }

    fun updateMembership(id: String, dto: MembershipDto) {
        val user = userRepository.findById(id)
            .orElseThrow { MembershipException(MembershipErrorCode.USER_NOT_FOUND) }

        val membership = user.membership
            ?: throw MembershipException(MembershipErrorCode.MEMBERSHIP_NOT_FOUND)

        membership.grade = dto.grade
        membership.startDate = dto.startDate
        membership.endDate = dto.endDate
        membership.autoRenew = dto.autoRenew

        userRepository.save(user)
    }

    fun updateCustomerKey(userId: String, customerKey: String) {
        val user = userRepository.findById(userId)
            .orElseThrow { MembershipException(MembershipErrorCode.USER_NOT_FOUND) }

        user.customerKey = customerKey
        userRepository.save(user)
    }
}
