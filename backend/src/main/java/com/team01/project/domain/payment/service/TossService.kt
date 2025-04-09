package com.team01.project.domain.payment.service

import com.team01.project.domain.payment.dto.TossSubscriptionRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class TossService {
    private val baseUrl = "https://api.tosspayments.com"

    @Value("\${toss.client-key}")
    lateinit var clientKey: String

    @Value("\${toss.secret-key}")
    lateinit var secretKey: String

    @Value("\${toss.webhook-secret}")
    lateinit var webhookSecret: String

    fun processSubscription(request: TossSubscriptionRequest): Boolean {
        val client = WebClient.create()

        val response = client.post()
            .uri("$baseUrl/v1/billing/$request.billingKey")
            .headers {
                val encodedKey = Base64.getEncoder().encodeToString("$secretKey:".toByteArray())
                it.setBasicAuth(encodedKey)
                it.contentType = MediaType.APPLICATION_JSON
            }
            .bodyValue(
                mapOf(
                    "amount" to request.amount,
                    "customerKey" to request.customerKey,
                    "orderId" to request.orderId,
                    "orderName" to request.orderName
                )
            )
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        println("Toss response: $response")
        return response?.contains("DONE") ?: false
    }
}
