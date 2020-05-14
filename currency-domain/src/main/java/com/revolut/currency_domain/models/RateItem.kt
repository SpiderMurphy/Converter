package com.revolut.currency_domain.models

data class RateItem(
    val stableId: Long,
    val currencyCode: String,
    val currencyName: String,
    val rateValue: Double
)
