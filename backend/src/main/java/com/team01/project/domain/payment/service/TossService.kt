package com.team01.project.domain.payment.service

import com.team01.project.domain.payment.dto.TossBillingResponse
import com.team01.project.domain.payment.dto.TossSubscriptionRequest
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

    fun processSubscription(request: TossSubscriptionRequest): TossBillingResponse? {
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
            val restTemplate = RestTemplate()
            val response = restTemplate.postForEntity(
                url,
                entity,
                TossBillingResponse::class.java
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
            val restTemplate = RestTemplate()
            val response = restTemplate.postForEntity(url, entity, Map::class.java)
            response.statusCode.is2xxSuccessful
        } catch (e: Exception) {
            println("자동 결제 실패: ${e.message}")
            false
        }
    }

}
