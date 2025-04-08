package com.team01.project.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openApi(): OpenAPI {
        val jwt = "JWT"
        val securityRequirement = SecurityRequirement().addList(jwt)
        val components = Components().addSecuritySchemes(
            jwt,
            SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        )
        return OpenAPI()
            .components(Components()) // 초기 Components 인스턴스 (기본값)
            .info(apiInfo())
            .addSecurityItem(securityRequirement)
            .components(components)  // 실제 보안 스키마가 포함된 Components 설정
    }

    private fun apiInfo(): Info {
        return Info()
            .title("음악 캘린더 API")        // API의 제목
            .description("음악 캘린더 서비스에 관한 API 문서화")  // API 설명
            .version("1.0.0")             // API 버전
    }
}