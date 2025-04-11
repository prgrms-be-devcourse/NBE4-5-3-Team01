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
            .openapi("3.0.1")
            .info(apiInfo())
            .components(components)
            .addSecurityItem(securityRequirement)
    }

    private fun apiInfo(): Info {
        return Info()
            .title("음악 캘린더 API") // API의 제목
            .description("음악 캘린더 서비스에 관한 API 문서화") // API 설명
            .version("1.0.0") // API 버전
    }
}
