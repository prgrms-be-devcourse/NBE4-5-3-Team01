package com.team01.project.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
	// 추가적으로 ThreadPoolTaskExecutor 등을 설정할 수 있습니다.
}
