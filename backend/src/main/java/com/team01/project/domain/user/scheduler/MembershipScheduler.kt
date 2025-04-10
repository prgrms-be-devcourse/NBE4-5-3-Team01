package com.team01.project.domain.user.scheduler

import com.team01.project.domain.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MembershipScheduler(
    private val userRepository: UserRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 4,16 * * *") // 매일 오전 4시, 오후 4시에 실행
    @Transactional
    fun downgradeExpiredMemberships() {
        val today = LocalDate.now()
        log.info("⭐ 멤버십 스케줄러 실행: {}", today)

        val expiredUsers = userRepository.findAll().filter {
            val m = it.membership
            m != null &&
                    m.grade == "premium" &&
                    !m.autoRenew &&
                    m.endDate?.isBefore(today) == true
        }

        expiredUsers.forEach { user ->
            val m = user.membership!!
            m.grade = "basic"
            m.startDate = null
            m.endDate = null
            m.billingKey = null

            log.info("요금제 강등: userId=${user.id}")
        }
    }
}
