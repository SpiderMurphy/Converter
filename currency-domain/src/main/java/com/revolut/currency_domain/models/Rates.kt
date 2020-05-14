package com.revolut.currency_domain.models

interface Rates {
    val baseCurrency: String
    val rates: Map<String, Double>
}
