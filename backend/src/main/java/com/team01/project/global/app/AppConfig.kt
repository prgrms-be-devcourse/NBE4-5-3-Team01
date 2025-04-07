package com.team01.project.global.app

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig(
    private val objectMapperInjected: ObjectMapper
) {

    @PostConstruct
    fun init() {
        objectMapper = objectMapperInjected
    }

    companion object {
        lateinit var objectMapper: ObjectMapper

        fun isNotProd(): Boolean = true

        fun getSiteFrontUrl(): String = "http://localhost:3000"
    }
}
