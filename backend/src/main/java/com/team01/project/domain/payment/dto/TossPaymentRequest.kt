package com.team01.project.domain.payment.dto

data class TossPaymentRequest(
    val paymentKey: String,
    val orderId: String,
    val amount: Int
)
