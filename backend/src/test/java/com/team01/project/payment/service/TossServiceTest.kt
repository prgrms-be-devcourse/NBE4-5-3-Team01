package com.team01.project.payment.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.team01.project.domain.payment.dto.TossPaymentRequest
import com.team01.project.domain.payment.dto.TossSubscriptionRequest
import com.team01.project.domain.payment.service.TossService
import com.team01.project.global.app.AppConfig
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.client.RestTemplate

class TossServiceTest {

    private lateinit var tossService: TossService
    private lateinit var mockWebServer: MockWebServer
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        AppConfig.objectMapper = ObjectMapper()

        tossService = TossService()
        ReflectionTestUtils.setField(tossService, "restTemplate", RestTemplate())
        ReflectionTestUtils.setField(tossService, "baseUrl", mockWebServer.url("/").toString())
        ReflectionTestUtils.setField(tossService, "secretKey", "test_secret")
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `1회 결제 성공 시 응답 반환`() {
        val mockResponse = MockResponse()
            .setBody("""{"orderId":"order123", "method":"카드", "approvedAt":"2025-04-10T13:00:00"}""")
            .setHeader("Content-Type", "application/json")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val request = TossPaymentRequest("payKey", "order123", 1000)
        val response = tossService.confirmPayment(request)

        assertEquals("order123", response?.orderId)
    }

    @Test
    fun `1회 결제 실패 시 null 반환`() {
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .addHeader("Content-Type", "application/json")
            .setBody("""{"code":400, "msg":"결제 실패"}""")
        mockWebServer.enqueue(mockResponse)

        val request = TossPaymentRequest("invalidKey", "failOrder", 1000)
        val result = tossService.confirmPayment(request)

        assertEquals(null, result)
    }

    @Test
    fun `정기 결제 billingKey 발급 성공`() {
        val mockResponse = MockResponse()
            .setBody("""{"billingKey":"billing123", "customerKey":"user123"}""")
            .setHeader("Content-Type", "application/json")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val request = TossSubscriptionRequest("auth123", "user123")
        val result = tossService.processSubscription(request)

        assertEquals("billing123", result?.billingKey)
    }

    @Test
    fun `정기 결제 billingKey 발급 실패`() {
        val mockResponse = MockResponse()
            .setResponseCode(500)
            .addHeader("Content-Type", "application/json")
            .setBody("""{"code":500, "msg":"billingKey 발급 실패"}""")
        mockWebServer.enqueue(mockResponse)

        val request = TossSubscriptionRequest("invalidAuth", "user123")
        val result = tossService.processSubscription(request)

        assertEquals(null, result)
    }

    @Test
    fun `정기 결제 청구 성공 시 true 반환`() {
        val mockResponse = MockResponse()
            .setBody("""{"status":"DONE"}""")
            .setHeader("Content-Type", "application/json")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val result = tossService.chargeBillingKey(
            billingKey = "billing123",
            customerKey = "user123",
            amount = 15000,
            orderName = "정기 결제"
        )

        assertTrue(result)
    }

    @Test
    fun `정기 결제 청구 실패 시 false 반환`() {
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .addHeader("Content-Type", "application/json")
            .setBody("""{"code":400, "msg":"청구 실패"}""")
        mockWebServer.enqueue(mockResponse)

        val result = tossService.chargeBillingKey(
            billingKey = "fail_billing",
            customerKey = "user123",
            amount = 15000,
            orderName = "정기 결제 실패"
        )

        assertTrue(result.not())
    }
}
