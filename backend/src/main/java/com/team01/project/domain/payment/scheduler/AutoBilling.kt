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
    private val tossService: TossService
) {
    private val log = LoggerFactory.getLogger(AutoBilling::class.java)

    @Scheduled(cron = "0 0 3,15 * * *") // 매일 오전 3시, 오후 3시에 실행
    @Transactional
    fun processAutoBilling() {
        val today = LocalDate.now()

        userRepository.findAll().forEach { user ->
            val membership = user.membership
            val billingKey = membership?.billingKey

            if (membership?.autoRenew == true &&
                billingKey != null &&
                membership.endDate?.isBefore(today) == true
            ) {
                val success = tossService.chargeBillingKey(user.id, billingKey, 1900, "정기 결제")

                membership.apply {
                    if (success) {
                        endDate = today.plusMonths(1)
                        failCount = 0
                        log.info("[INFO] ${user.name}(${user.id})의 자동 결제 성공. 종료일 연장됨 → $endDate")
                    } else {
                        failCount++
                        log.info("[INFO] ${user.name}(${user.id})의 자동 결제 실패. 누적 실패 횟수: $failCount")

                        if (failCount >= 3) {
                            autoRenew = false
                            log.info("[INFO] ${user.name}(${user.id})의 자동 결제 3회 실패로 autoRenew 해제")
                        }
                    }
                }
            }
            userRepository.save(user)
        }
    }
}
