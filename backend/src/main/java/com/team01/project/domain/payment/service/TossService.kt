package com.team01.project.domain.payment.service

import com.team01.project.domain.payment.dto.TossPaymentRequest
import com.team01.project.domain.payment.dto.TossPaymentResponse
import com.team01.project.domain.payment.dto.TossSubscriptionRequest
import com.team01.project.domain.payment.dto.TossSubscriptionResponse
import com.team01.project.global.app.AppConfig.Companion.objectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
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

    val restTemplate = RestTemplate()

    fun confirmPayment(request: TossPaymentRequest): TossPaymentResponse? {
        val url = "$baseUrl/v1/payments/confirm"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            val encoded = Base64.getEncoder().encodeToString("$secretKey:".toByteArray())
            set("Authorization", "Basic $encoded")
        }

        val body = mapOf(
            "paymentKey" to request.paymentKey,
            "orderId" to request.orderId,
            "amount" to request.amount
        )

        val entity = HttpEntity(body, headers)

        return try {
            val response = restTemplate.postForEntity(url, entity, TossPaymentResponse::class.java)
            val res = response.body

            println("✅ Toss 응답 내용: ${objectMapper.writeValueAsString(res)}")

            res
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun processSubscription(request: TossSubscriptionRequest): TossSubscriptionResponse? {
        val url = "https://api.tosspayments.com/v1/billing/authorizations/issue"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            val encodedSecret = Base64.getEncoder()
                .encodeToString("$secretKey:".toByteArray())
            set("Authorization", "Basic $encodedSecret")
        }

        val body = mapOf(
            "authKey" to request.authKey,
            "customerKey" to request.customerKey
        )

        val entity = HttpEntity(body, headers)

        return try {
            val response = restTemplate.postForEntity(
                url,
                entity,
                TossSubscriptionResponse::class.java
            )
            response.body
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun chargeBillingKey(
        billingKey: String,
        customerKey: String,
        amount: Int,
        orderName: String
    ): Boolean {
        val url = "$baseUrl/v1/billing/$billingKey"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            val encodedSecret = Base64.getEncoder()
                .encodeToString("$secretKey:".toByteArray())
            set("Authorization", "Basic $encodedSecret")
        }

        val orderId = "auto_" + System.currentTimeMillis()

        val body = mapOf(
            "customerKey" to customerKey,
            "amount" to amount,
            "orderId" to orderId,
            "orderName" to orderName
        )

        val entity = HttpEntity(body, headers)

        return try {
            val response = restTemplate.postForEntity(url, entity, Map::class.java)
            response.statusCode.is2xxSuccessful
        } catch (e: Exception) {
            println("자동 결제 실패: ${e.message}")
            false
        }
    }
}
