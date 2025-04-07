package com.team01.project.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
@EnableScheduling
class SchedulingConfig {
    @Bean
    fun taskScheduler(): ThreadPoolTaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 5 // 필요에 따라 스레드 풀 크기 조절
        scheduler.setThreadNamePrefix("NotificationScheduler-")
        scheduler.initialize() // 명시적으로 초기화
        return scheduler
    }

    @Bean
    fun separateTaskScheduler(): ThreadPoolTaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 5
        scheduler.setThreadNamePrefix("separate-scheduler-")
        scheduler.initialize()
        return scheduler
    }
}
