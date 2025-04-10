package com.team01.project.domain.payment.dto

data class TossPaymentResponse(
    val orderId: String,
    val method: String,
    val approvedAt: String
)
