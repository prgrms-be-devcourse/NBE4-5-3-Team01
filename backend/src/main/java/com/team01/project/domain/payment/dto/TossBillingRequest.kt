package com.team01.project.domain.payment.dto

data class TossBillingRequest(
    val customerKey: String,
    val cardNumber: String,
    val cardExpirationYear: String,
    val cardExpirationMonth: String,
    val cardPassword: String,
    val identityNumber: String
)
