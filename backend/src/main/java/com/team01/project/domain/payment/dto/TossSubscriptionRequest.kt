package com.team01.project.domain.payment.dto

data class TossSubscriptionRequest(
    val customerKey: String,
    val billingKey: String,
    val amount: Int,
    val orderId: String,
    val orderName: String
)
