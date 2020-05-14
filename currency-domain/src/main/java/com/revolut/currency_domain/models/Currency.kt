package com.revolut.currency_domain.models

data class Currency(
    val currencyCode: String,
    val amount: Double,
    val rate: Double
)
