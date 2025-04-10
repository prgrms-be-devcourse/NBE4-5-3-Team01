package com.team01.project.domain.payment.dto

data class TossSubscriptionRequest(
    val authKey: String,
    val customerKey: String,
    val amount: Int,
    val orderId: String,
    val orderName: String,
)
