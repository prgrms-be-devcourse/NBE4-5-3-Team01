package com.team01.project.domain.payment.scheduler

import com.team01.project.domain.payment.service.TossService
import com.team01.project.domain.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AutoBilling(
    private val userRepository: UserRepository,
    private val tossService: TossService,
) {
    private val log = LoggerFactory.getLogger(AutoBilling::class.java)

    @Scheduled(cron = "0 0 3,15 * * *") // 매일 오전 3시, 오후 3시에 실행
    @Transactional
    fun processAutoBilling() {
        val today = LocalDate.now()

        val users = userRepository.findAll().filter {
            val m = it.membership
            m != null && m.autoRenew && m.billingKey != null && m.endDate?.isBefore(today) == true
        }

        for (user in users) {
            val membership = user.membership!!
            val success = tossService.chargeBillingKey(
                billingKey = membership.billingKey!!,
                customerKey = user.customerKey!!,
                amount = 1900,
                orderName = "프리미엄 멤버십"
            )

            if (success) {
                membership.endDate = today.plusMonths(1)
                user.membership!!.count += 1
                membership.failCount = 0
                log.info("자동 결제 성공: userId=${user.id}, 다음 종료일=${membership.endDate}")
            } else {
                membership.failCount += 1
                log.warn("자동 결제 실패: userId=${user.id}, 실패 횟수=${membership.failCount}")

                // ✅ 3회 이상 실패 시 자동 갱신 해제
                if (membership.failCount >= 3) {
                    membership.autoRenew = false
                    log.warn("자동 갱신 해제: userId=${user.id}, 실패 3회 이상")
                }
            }

            userRepository.save(user)
        }
    }

}