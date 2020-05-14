package com.revolut.currency_domain.models

data class BaseAndSelectedCurrency(
    val baseCurrency: Currency,
    val selectedCurrency: Currency
)
